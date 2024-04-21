

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import ci.miage.mob.networkls.NetworkObject

class NetworkConnection{


var startConnexion = PointF()
var endConnexion = PointF()
var color = Paint().apply { color=Color.RED }
var startObjet = NetworkObject("lal",12f,13f,Color.RED, rect = RectF(),10f,color, )
var endObjet = NetworkObject("lal",12f,13f,Color.RED, rect = RectF(),10f,color, )
var path = Path()
var connectionPaint = Paint().apply {
    color = Color.BLUE
    style = Paint.Style.STROKE
    strokeWidth = 10f
}
}
