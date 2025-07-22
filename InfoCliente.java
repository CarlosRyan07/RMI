import java.io.Serializable;

/**
 * Classe para armazenar informações de um cliente do chat
 * Implementa Serializable para poder ser enviada via RMI
 */
public class InfoCliente implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String apelido;
    private String ip;
    private int porta;
    private long timestampConexao;
    
    public InfoCliente(String apelido, String ip, int porta) {
        this.apelido = apelido;
        this.ip = ip;
        this.porta = porta;
        this.timestampConexao = System.currentTimeMillis();
    }
    
    // Getters
    public String getApelido() {
        return apelido;
    }
    
    public String getIp() {
        return ip;
    }
    
    public int getPorta() {
        return porta;
    }
    
    public long getTimestampConexao() {
        return timestampConexao;
    }
    
    // Setters
    public void setApelido(String apelido) {
        this.apelido = apelido;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public void setPorta(int porta) {
        this.porta = porta;
    }
    
    @Override
    public String toString() {
        return String.format("@%s (%s:%d)", apelido, ip, porta);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        InfoCliente that = (InfoCliente) obj;
        return apelido != null ? apelido.equals(that.apelido) : that.apelido == null;
    }
    
    @Override
    public int hashCode() {
        return apelido != null ? apelido.hashCode() : 0;
    }
}
