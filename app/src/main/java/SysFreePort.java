import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

public class SysFreePort {
    private static Random random = new Random();

    private Socket socket;

    /**

     *

     * 获取系统空闲端口

     *

     *

     * 获取系统空闲端口，并占用该端口资源

     *

     *

     *@throws IOException

     */

    public static SysFreePort custom() throws IOException {
        return new SysFreePort();

    }

    private SysFreePort() throws IOException {
        socket = new Socket();

        InetSocketAddress inetAddress = new InetSocketAddress(0);

        socket.bind(inetAddress);

    }

    /**

     *

     * 释放端口资源

     *

     *

     * 释放该端口资源

     *

     *

     *@throws IOException

     */

    public void freePort() throws IOException {
        if (null == socket || socket.isClosed()) {
            return;

        }

        socket.close();

    }

    /**

     *

     * 返回端口

     *

     *

     * 返回端口，不释放该端口资源

     *

     */

    public int getPort() {
        if (null == socket || socket.isClosed()) {
            return -1;

        }

        return socket.getLocalPort();

    }

    /**

     *

     * 返回端口

     *

     *

     * 返回端口并释放该端口资源

     *

     *

     *@throws IOException

     */

    public int getPortAndFree() throws IOException {
        if (null == socket || socket.isClosed()) {
            return -1;

        }

        int port = socket.getLocalPort();

        socket.close();

        return port;

    }

    /**

     *

     * 生成随机port

     *

     *

     * 在[start, end)间随机生成一个数字作为port

     *

     *

     *@param start

     *@param end

     *@return int

     */

    public static int random(int start, int end) {
        return random.nextInt(Math.abs(end - start)) + start;

    }

}
