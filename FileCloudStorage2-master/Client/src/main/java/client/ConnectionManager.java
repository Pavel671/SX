package client;

import common.Message;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;


public class ConnectionManager implements Closeable {

    private static ConnectionManager connectionManager;
    private final Socket socket;
    private final ObjectEncoderOutputStream outOES;
    private final ObjectDecoderInputStream inODS;

    private ConnectionManager(Socket socket) throws IOException {
        this.socket = socket;
        this.outOES = new ObjectEncoderOutputStream(socket.getOutputStream());
        this.inODS = new ObjectDecoderInputStream(socket.getInputStream());
    }

    public static synchronized ConnectionManager getConnectionManager(Socket socket) throws IOException {
        if (connectionManager == null) connectionManager = new ConnectionManager(socket);
        return connectionManager;
    }


    public void send(Message message) throws IOException {
        synchronized (outOES){
            outOES.writeObject(message);
            outOES.flush();
        }
    }


    public Message receive() throws IOException, ClassNotFoundException {
        Message msg;
        synchronized (inODS){
            msg = (Message) inODS.readObject();
        }
        return msg;
    }



    public SocketAddress getRemoteSocketAddress(){
        return socket.getRemoteSocketAddress();
    }


    @Override
    public void close() throws IOException {
        inODS.close();
        outOES.close();
        socket.close();
    }
}