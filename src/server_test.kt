import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class Server:Thread  {
    val server = ServerSocket(5050)
    var port: Int = 65536
    var serverConnection = true
    var connection = true
    val input_Stream = LinkedBlockingQueue<String>()
    val output_Stream = LinkedBlockingQueue<String>()
    var scanner: Scanner? = Scanner(System.`in`)
    var time_u: TimeUnit = TimeUnit.MILLISECONDS

    constructor(port: Int) {
        this.port = port
    }
    override fun run(){
        this.activate()
    }
    fun activate() {
        while (serverConnection) {
            connection = true
            val client = server.accept()
            var data_in = DataInputStream(client.getInputStream())
            var data_out = DataOutputStream(client.getOutputStream())
            var recv = thread(start = false) { this.recv(data_in) }
            var send = thread(start = false) { this.send(data_out) }
            var scar = thread(start = false) { this.listenKeyboard() }
            var prtin = thread(start = false) { this.dosomething() }
            recv.start()
            send.start()
            prtin.start()
            scar.start()
            recv.join()
            send.join()
            prtin.join()
            scar.join()
            client.close()
        }

    }

    fun recv(data_in: DataInputStream) {
        while (this.connection) {
            try {
                this.recv_msg(data_in)
            } catch (e: Exception) {
                this.connection = false
                println(e)
            }
        }
    }

    fun recv_msg(data_in: DataInputStream) {
        var head = data_in.readInt()
        var msg = ByteArray(head)
        data_in.readFully(msg, 0, head)
        var data = msg.decodeToString()
        input_Stream.offer(data, 1000, time_u)
    }

    fun dosomething() {
        while (this.connection) {
            var data = input_Stream.poll(1000, time_u)
            if (data!=null){
                println(data+"this is server catch")
            }
        }
    }

    fun send(data_out: DataOutputStream) {
        while (this.connection) {
            try {
                this.send_msg(data_out)
            } catch (e: Exception) {
                this.connection = false
                println(e)
            }
        }
    }

    fun listenKeyboard() {
        while (this.connection) {
            var rd=(0..1000).random()
            var data=rd.toString()
            output_Stream.offer(data,1000,time_u)
            Thread.sleep(5000)
        }
    }

    fun send_msg(data_out: DataOutputStream) {

        var data = output_Stream.poll(1000, time_u)
        if (data != null) {
            var data_2 = data.encodeToByteArray()
            data_out.writeInt(data_2.size)
            data_out.write(data_2)
        }

    }

}


