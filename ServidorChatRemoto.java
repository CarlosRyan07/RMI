import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Interface remota para o servidor de chat
 * Gerencia o registro de clientes e fornece informações para conexões P2P
 */
public interface ServidorChatRemoto extends Remote {
    
    /**
     * Registra um cliente no servidor de chat
     * @param apelido @ único do cliente
     * @param ip endereço IP do cliente
     * @param porta porta onde o cliente está escutando
     * @return true se registrado com sucesso, false se @ já existe
     */
    boolean registrarCliente(String apelido, String ip, int porta) throws RemoteException;
    
    /**
     * Remove um cliente do servidor (quando sai do chat)
     * @param apelido @ do cliente a ser removido
     */
    void removerCliente(String apelido) throws RemoteException;
    
    /**
     * Busca informações de um cliente específico
     * @param apelido @ do cliente procurado
     * @return informações do cliente ou null se não encontrado
     */
    InfoCliente buscarCliente(String apelido) throws RemoteException;
    
    /**
     * Retorna lista de todos os clientes ativos
     * @return lista com informações de todos os clientes conectados
     */
    List<InfoCliente> listarClientesAtivos() throws RemoteException;
    
    /**
     * Envia mensagem para todos os clientes (broadcast na sala)
     * @param remetente @ de quem está enviando
     * @param mensagem conteúdo da mensagem
     */
    void enviarMensagemSala(String remetente, String mensagem) throws RemoteException;
}
