import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

var time_u: TimeUnit = TimeUnit.MILLISECONDS
val input_string = LinkedBlockingQueue<String>()
fun main(args: Array<String>) {
    var server_th=Server(5050)
    var client_th=Client("127.0.0.1",5050)

    server_th.start()
    Thread.sleep(5000)
    client_th.start()

}