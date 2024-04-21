

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import android.view.GestureDetector
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GestureDetectorCompat
import ci.miage.mob.networkls.Mode
import ci.miage.mob.networkls.NetworkObject
import com.google.gson.Gson
import ci.miage.mob.networkls.R

class Graph(context: Context) : View(context),GestureDetector.OnGestureListener {
    private var objet = NetworkObject("ll", 12f, 15f,)
    private var objets: MutableList<NetworkObject> = mutableListOf()
    private var connexions: MutableList<NetworkConnection> = mutableListOf()
    var mode: Mode = Mode.ADD
    private var startObject: NetworkObject? = null
    private val tempPath: Path = Path()
    private var isDragging: Boolean = false
    private val gestureDetector = GestureDetectorCompat(context, this)
    private var isCreatingConnection: Boolean = false
    private var draggingObject: NetworkObject= ci.miage.mob.networkls.NetworkObject("ll", 12f, 15f,)
    private var findObjet: Boolean = false
    override fun onDown(e: MotionEvent): Boolean {
return true
    }

    override fun onShowPress(e: MotionEvent) {
        //TODO("Not yet implemented")
        //  Log.d("OnShowPress","OnShowPress")
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return true
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return true
    }

    override fun onLongPress(p0: MotionEvent) {

        val touchedObject = findObjectAtPoint(p0.x, p0.y)
          if (touchedObject != null) {
            editObjectMenu(touchedObject)
        }

        if (mode == Mode.ADD){
            val alertDialog = AlertDialog.Builder(context).setTitle(R.string.alerteDialog_title).setMessage(R.string.alerteDialog_message)
            val editText = EditText(context)
            alertDialog.setView(editText)
            alertDialog.setPositiveButton(R.string.alerteDialog_confirm) { dialog, which ->
                objet =createObjetAtPositionWithLabel(p0,editText.text.toString())
               objets.add(objet)
                invalidate()
            }
            alertDialog.setNegativeButton(R.string.alerteDialog_cancel) { dialog, which ->
                dialog.cancel()
            }
            alertDialog.show()
            Log.d("Objets : = ",objets.toString())
        }
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
       return true
    }

    override fun onDraw(canvas: Canvas) {
        if (objets.isNotEmpty()){
            for (obj in objets){
                canvas.drawRoundRect(obj.rect, obj.cornerRadius, obj.cornerRadius, obj.paint)
                // dessiner le label
                canvas.drawText(obj.name, obj.rect.centerX(), obj.position.y + obj.labelPositionY, obj.labelStyle)
            }
            // Dessiner les connexions existantes
            for (connexion in connexions) {
                canvas.drawLine(
                    connexion.startConnexion.x, connexion.startConnexion.y,
                    connexion.endConnexion.x, connexion.endConnexion.y,
                    connexion.connectionPaint
                )
            }
            if (isCreatingConnection) {
                // Dessinez la connexion temporaire
                canvas.drawPath(tempPath, Paint().apply {
                    color = Color.BLUE
                    style = Paint.Style.STROKE
                    strokeWidth = 10f
                })
            }

        }
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Faire un when sur le mode
        when (mode){
            Mode.ADD->{
                if (gestureDetector.onTouchEvent(event)) {
                    return true // si le geste est detecté on retourne true
                }
            }
            Mode.EDIT->{
                if (gestureDetector.onTouchEvent(event)) {
                    return true // si le geste est detecté on retourne true
                }
            }
            Mode.CONNECT->{
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        //verifier si l'objet est selectionné
                        val touchedObject = findObjectAtPoint(event.x, event.y)
                        if (touchedObject !=null){
                            isCreatingConnection = true
                            startObject = touchedObject
                            tempPath.reset()
                            tempPath.moveTo(touchedObject.rect.centerX(), touchedObject.rect.centerY())
                        }
                        return true

                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (isCreatingConnection) {
                            // ici je met à jour le chemin temporaire à chaque mouvement du doigt
                            tempPath.lineTo(event.x, event.y)
                            invalidate()
                        }
                        return true

                    }
                    MotionEvent.ACTION_UP -> {
                        if (isCreatingConnection) {
                            // Vérifiez si le doigt est relâché sur un autre objet
                            val endObject = findObjectAtPoint(event.x, event.y)
                            if (endObject != null && endObject != startObject) {
                                // Créez la connexion entre les deux objets
                                createConnection(startObject, endObject)
                            }
                            isCreatingConnection = false
                            tempPath.reset()
                            invalidate()
                        }
                        return true

                    }
                }


            }
            Mode.MOVE->{
                when (event.action) {

                    MotionEvent.ACTION_DOWN -> {
                        val touchedObject = findObjectAtPoint(event.x, event.y)
                        if (touchedObject != null) {
                            isDragging = true
                            findObjet = true
                            draggingObject = touchedObject
                        }
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val drawZoneHeight = height.toFloat() // la hauteur de la zone de dessin - la hauteur de la toolbar
                        val drawZoneWidth = width.toFloat()
                        if (isDragging) {
                            if (findObjet) {
                                val newPositionAfterDrag = PointF(event.x, event.y)
                                Log.d("INNOCENT : drawZoneHeight", drawZoneWidth.toString())
                                val offsetX = newPositionAfterDrag.x - draggingObject.position.x
                                val offsetY = newPositionAfterDrag.y - draggingObject.position.y
                                draggingObject.position = newPositionAfterDrag
                                draggingObject.rect = RectF(
                                    newPositionAfterDrag.x,
                                    newPositionAfterDrag.y,
                                    newPositionAfterDrag.x + 150f,
                                    newPositionAfterDrag.y + 100f
                                )
                                for (connexion in connexions) {
                                    if (connexion.startObjet == draggingObject) {
                                        connexion.startConnexion.offset(offsetX, offsetY)
                                    }
                                    if (connexion.endObjet == draggingObject) {
                                        connexion.endConnexion.offset(offsetX, offsetY)
                                    }
                                }
                                invalidate()
                            }
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        isDragging = false
                        // draggingObject = null
                        findObjet = false
                        startObject = null
                        return true
                    }
                }

            }

        }
        return super.onTouchEvent(event)
    }
    private fun findObjectAtPoint(x: Float, y: Float): NetworkObject? {
        for (objet in objets) {
            if (objet.rect.contains(x, y)) {
                return objet
            }
        }
        return null
    }
    private fun createConnection(start: NetworkObject?, end: NetworkObject?) {
        if (start != null && end != null) {
            val connection = createConnectioninobj(start,end)
           connexions.add(connection)
            invalidate()
        }
    }
    fun createConnectioninobj(start: NetworkObject, end: NetworkObject): NetworkConnection{
        // Créez une connexion entre les objets avec une ligne droite
        val connexion = NetworkConnection()
        connexion.startObjet = start
        connexion.endObjet = end
        // Calculez les coordonnées de début et de fin de la ligne droite
        connexion.startConnexion = PointF(start.position.x + 150f, start.position.y + 100f)
        connexion.endConnexion = PointF(end.position.x + 150f, end.position.y + 100f)
        return connexion
    }

    fun createObjetAtPositionWithLabel(event: MotionEvent?, name :String) :NetworkObject{
        var color = Paint().apply { color=Color.RED }
        val newObjet = ci.miage.mob.networkls.NetworkObject(
            "lal",
            12f,
            13f,
            Color.RED,
            rect = RectF(),
            10f,
            color
        )
        if (event != null) {

            val newPositionX = event.x
            val newPositionY = event.y
            newObjet.position = PointF(newPositionX, newPositionY)
            val newRect = RectF(newPositionX, newPositionY, newPositionX + NetworkObject.rectWidth, newPositionY + NetworkObject.rectHeight)
            newObjet.rect = newRect
            newObjet.name = name
        }
        return newObjet
    }
    fun saveGraph() {
        if (objets.isNotEmpty()) {

            val gson = Gson()
            val networkobjectgson=gson.toJson(objets)
            //val graphJson = gson.toJson(graph)
            val sharedPreferences = context.getSharedPreferences("objets", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("objets",networkobjectgson)
            editor.apply()
            Toast.makeText(context, "Votre reseau a bien été enregistré", Toast.LENGTH_SHORT).show()
        }
    }
    private fun popupMenuObjet(objet: NetworkObject,drawZone: Graph) {
        val popupMenu = PopupMenu(context, drawZone, Gravity.START)
        //popupMenu.inflate(R.menu.context_menu_objet)
        popupMenu.setOnMenuItemClickListener { item: MenuItem? ->
            when (item?.itemId) {
                R.id.edit_object -> {
                    //afficher une boite de dialogue pour saisir le nom de l'objet
                    val alertDialog =
                        AlertDialog.Builder(context).setTitle(R.string.alerteDialog_title)
                            .setMessage(R.string.alerteDialog_message)
                    val editText = EditText(context)
                    alertDialog.setView(editText)
                    alertDialog.setPositiveButton(R.string.alerteDialog_confirm) { dialog, which ->
                        objet.name = editText.text.toString()
                        invalidate()
                    }
                    alertDialog.setNegativeButton(R.string.alerteDialog_cancel) { dialog, which ->
                        dialog.cancel()
                    }
                    alertDialog.show()
                    true
                }

                //R.id.delete_object -> {
                   // graph.objets.remove(objet)
                   // invalidate()
                   // true
               // }

                else -> false
            }
        }
        popupMenu.show()


    }
    private fun editObjectMenu(objet: NetworkObject) {
        // Charger le layout personnalisé de la boîte de dialogue
        val alertDialogLayout = LayoutInflater.from(context).inflate(R.layout.editlayout, null)

        // Créer la boîte de dialogue
        val alertDialog = AlertDialog.Builder(context)
            .setTitle(R.string.alerteDialog_edit_menu_title)
            .setView(alertDialogLayout) // Utilisez setView pour définir la vue

        val editText = alertDialogLayout.findViewById<EditText>(R.id.editTextObjectName)
        val redColor = alertDialogLayout.findViewById<RadioButton>(R.id.radioButtonRed)
        val greenColor = alertDialogLayout.findViewById<RadioButton>(R.id.radioButtonGreen)
        val blueColor = alertDialogLayout.findViewById<RadioButton>(R.id.radioButtonBlue)
        val magentaColor = alertDialogLayout.findViewById<RadioButton>(R.id.radioButtonMagenta)
        val cyanColor = alertDialogLayout.findViewById<RadioButton>(R.id.radioButtonCyan)
        val orangeColor = alertDialogLayout.findViewById<RadioButton>(R.id.radioButtonYellow)
        val deleteObjet = alertDialogLayout.findViewById<RadioButton>(R.id.radioButtonDelete)

        // obtenir les references des boutons dans le layout de la boite de dialogue
         editText.setText(objets[objets.indexOf(objet)].name)
        when (objets[objets.indexOf(objet)].color){
            Color.RED -> redColor.isChecked = true
            Color.GREEN -> greenColor.isChecked = true
            Color.BLUE -> blueColor.isChecked = true
            Color.MAGENTA -> magentaColor.isChecked = true
            Color.CYAN -> cyanColor.isChecked = true
            Color.YELLOW -> orangeColor.isChecked = true
        }
        alertDialog.setPositiveButton(R.string.alerteDialog_confirm) { _, _ ->
            objet.name = editText.text.toString()
            if (greenColor.isChecked){
                objet.color = Color.GREEN
                objets[objets.indexOf(objet)].color = objet.color
                objet.paint.color = objet.color
                objets[objets.indexOf(objet)].paint.color = objet.color

            }
            if (redColor.isChecked){
                objet.color = Color.RED
                objets[objets.indexOf(objet)].color = objet.color
                objet.paint.color = objet.color
                objets[objets.indexOf(objet)].paint.color = objet.color

            }
            if (blueColor.isChecked){
                objet.color = Color.BLUE
                objets[objets.indexOf(objet)].color = objet.color
                objet.paint.color = objet.color
                objets[objets.indexOf(objet)].paint.color = objet.color

            }
            if (magentaColor.isChecked){
                objet.color = Color.MAGENTA
                objets[objets.indexOf(objet)].color = objet.color
                objet.paint.color = objet.color
                objets[objets.indexOf(objet)].paint.color = objet.color

            }
            if (cyanColor.isChecked){
                objet.color = Color.CYAN
                objets[objets.indexOf(objet)].color = objet.color
                objet.paint.color = objet.color
                objets[objets.indexOf(objet)].paint.color = objet.color

            }
            if (orangeColor.isChecked){
                objet.color = Color.YELLOW
               objets[objets.indexOf(objet)].color = objet.color
                objet.paint.color = objet.color
                objets[objets.indexOf(objet)].paint.color = objet.color

            }

            if (deleteObjet.isChecked){
               objets.remove(objet)
                connexions.removeIf { it.startObjet == objet || it.endObjet == objet }
            }


            invalidate()
        }
        alertDialog.setNegativeButton(R.string.alerteDialog_cancel) { dialog, which ->
            dialog.cancel()
        }
        // Afficher la boîte de dialogue
        alertDialog.show()
    }
    /*fun viewSavedNetwork() {
        val sharedPreferences = context.getSharedPreferences("objets", Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            val graphJson = sharedPreferences.getString("objets", null)
            if (graphJson != null) {
                drawNetwork(graphJson)
            }
        }

    }
    private fun drawNetwork(graphJson: String){

        val gson = Gson()
        val graphRestored = gson.fromJson(graphJson, Graph::class.java) // convertir le json en objet graph
        for (connexion in graphRestored.connexions) {
            connexion.connectionPaint = Paint().apply {
                color = Color.BLUE
                style = Paint.Style.STROKE
                strokeWidth = Connexion.smallStroke
            }
            connexion.path = Path()
            connexion.path.apply {
                reset()
                moveTo(connexion.startConnexionX, connexion.startConnexionY)
                lineTo(connexion.endConnexionX, connexion.endConnexionY)
            }
            connexion.labelStyle.apply {
                Paint.Style.FILL
                Color.BLACK
                textSize = 30f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }

        }
        for (objet in graphRestored.objets){
            objet.rect = RectF(
                objet.position.x,
                objet.position.y,
                objet.position.x + Objet.rectWidth,
                objet.position.y + Objet.rectHeight
            )
            objet.paint = Paint().apply {
                color = objet.color
                style = Paint.Style.FILL
            }
        }
        this.graph = graphRestored // mettre à jour le graph de la zone de dessin
        Log.d("Graph", graph.toString())
        invalidate() // redessiner la zone de dessin
    }*/
}