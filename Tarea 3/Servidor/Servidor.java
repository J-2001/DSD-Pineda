public class Servidor {
    
    private final int port;
    
    public Servidor(int port) {
        this.port = port;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public static void main(String[] args) {
        int puerto = 8080;
        if (args.length == 1) {
            puerto = Integer.valueOf(args[0]);
        }
        
        Servidor servidor = new Servidor(puerto);
        
        System.out.println("Servidor iniciado en el puerto " + servidor.getPort() + "...");
    }
    
}
