package ci.miage.mob.networkls

import Graph
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    lateinit var graph: Graph
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar) //ajout de la toolbar
        graph=Graph(this)
        val drawView = findViewById<FrameLayout>(R.id.drawZone)
        drawView.addView(graph)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //Ajout des elements au menu
        val inflater = this.menuInflater
        inflater.inflate(R.menu.toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.reset -> {
                this.recreate()
                Toast.makeText(this, R.string.reset, Toast.LENGTH_SHORT).show()
                true
            }

            R.id.add_object -> {
                graph.mode = Mode.ADD
                Toast.makeText(this, R.string.add_object, Toast.LENGTH_SHORT).show()
                true
            }

            R.id.add_connexion -> {
                graph.mode = Mode.CONNECT
                Toast.makeText(this, R.string.add_connexion, Toast.LENGTH_SHORT).show()
                true
            }

            R.id.edit_object -> {
                graph.mode = Mode.EDIT
                Toast.makeText(this, R.string.edit_object, Toast.LENGTH_SHORT).show()
                true
            }

            R.id.move_object -> {
                this.graph.mode = Mode.MOVE
                Toast.makeText(this, R.string.move_object, Toast.LENGTH_SHORT).show()
                true
            }

            R.id.display -> {
                /*graph.viewSavedNetwork()
                Toast.makeText(this,R.string.view_saved_network, Toast.LENGTH_SHORT).show()*/
                true

            }

            R.id.save -> {

                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }
}