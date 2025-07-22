import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface remota para comunicação P2P entre clientes
 */
public interface ClienteChatRemoto extends Remote {
    
    /**
     * Recebe uma mensagem de texto de outro cliente
     * @param remetente @ de quem enviou a mensagem
     * @param mensagem conteúdo da mensagem
     */
    void receberMensagem(String remetente, String mensagem) throws RemoteException;
    
    /**
     * Recebe uma mensagem da sala (broadcast)
     * @param remetente @ de quem enviou a mensagem
     * @param mensagem conteúdo da mensagem
     */
    void receberMensagemSala(String remetente, String mensagem) throws RemoteException;
    
    /**
     * Recebe um arquivo de outro cliente
     * @param remetente @ de quem enviou o arquivo
     * @param nomeArquivo nome do arquivo
     * @param conteudo conteúdo do arquivo em bytes
     */
    void receberArquivo(String remetente, String nomeArquivo, byte[] conteudo) throws RemoteException;
    
    /**
     * Verifica se o cliente está ativo (para teste de conectividade)
     * @return true se ativo
     */
    boolean estaAtivo() throws RemoteException;
}
