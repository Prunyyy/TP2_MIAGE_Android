package ci.miage.mob.networkls

import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.view.MotionEvent

data class NetworkObject(

    var name: String,
    var x: Float,
    var y: Float,
    var color: Int = Color.BLUE,
    var rect: RectF = RectF(),
    val cornerRadius: Float = 10f,
    val paint:  Paint = Paint(),
    var position: PointF = PointF(),
    val labelPositionY: Float=  50+30f,
    var labelStyle: Paint = Paint().apply { textSize=50f }

) {
    companion object {
        val paint = Paint() // permet de dessiner des formes
        const val cornerRadius = 10f // Rayon des coins en pixels
        const val rectWidth = 150f
        const val rectHeight = 100f
        const val labelPositionX =  rectWidth+30f
        const val labelPositionY =  rectHeight+30f
        var labelStyle = Paint()
    }


}

