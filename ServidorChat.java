import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementação do servidor de chat RMI
 * Gerencia os clientes conectados e facilita a comunicação P2P
 */
public class ServidorChat extends UnicastRemoteObject implements ServidorChatRemoto {
    private static final long serialVersionUID = 1L;
    
    // Mapa thread-safe para armazenar clientes ativos
    private ConcurrentHashMap<String, InfoCliente> clientesAtivos;
    
    public ServidorChat() throws RemoteException {
        super();
        this.clientesAtivos = new ConcurrentHashMap<>();
        System.out.println("Servidor de Chat iniciado!");
    }
    
    @Override
    public boolean registrarCliente(String apelido, String ip, int porta) throws RemoteException {
        // Verifica se o apelido já existe
        if (clientesAtivos.containsKey(apelido)) {
            System.out.println("Tentativa de registro rejeitada - @ já existe: " + apelido);
            return false;
        }
        
        // Registra o novo cliente
        InfoCliente novoCliente = new InfoCliente(apelido, ip, porta);
        clientesAtivos.put(apelido, novoCliente);
        
        System.out.println("Cliente registrado: " + novoCliente);
        System.out.println("Total de clientes ativos: " + clientesAtivos.size());
        
        return true;
    }
    
    @Override
    public void removerCliente(String apelido) throws RemoteException {
        InfoCliente clienteRemovido = clientesAtivos.remove(apelido);
        if (clienteRemovido != null) {
            System.out.println("Cliente removido: " + clienteRemovido);
            System.out.println("Total de clientes ativos: " + clientesAtivos.size());
        } else {
            System.out.println("Tentativa de remover cliente inexistente: @" + apelido);
        }
    }
    
    @Override
    public InfoCliente buscarCliente(String apelido) throws RemoteException {
        System.out.println("Buscando cliente: @" + apelido);
        System.out.println("Clientes cadastrados: " + clientesAtivos.keySet());
        
        InfoCliente cliente = clientesAtivos.get(apelido);
        if (cliente != null) {
            System.out.println("✓ Cliente encontrado: " + cliente);
        } else {
            System.out.println("✗ Cliente não encontrado: @" + apelido);
        }
        return cliente;
    }
    
    @Override
    public List<InfoCliente> listarClientesAtivos() throws RemoteException {
        List<InfoCliente> lista = new ArrayList<>(clientesAtivos.values());
        System.out.println("Listagem de clientes solicitada. Total: " + lista.size());
        return lista;
    }
    
    @Override
    public void enviarMensagemSala(String remetente, String mensagem) throws RemoteException {
        System.out.println("Broadcast de @" + remetente + ": " + mensagem);
        System.out.println("Enviando para " + clientesAtivos.size() + " clientes cadastrados");
        
        // Envia para todos os clientes ativos
        for (InfoCliente cliente : clientesAtivos.values()) {
            // Não envia para o próprio remetente
            if (!cliente.getApelido().equals(remetente)) {
                try {
                    // Conecta ao cliente via RMI
                    String urlCliente = "//" + cliente.getIp() + ":" + cliente.getPorta() + "/Cliente_" + cliente.getApelido();
                    System.out.println("Tentando conectar a: " + urlCliente);
                    
                    ClienteChatRemoto clienteRemoto = (ClienteChatRemoto) Naming.lookup(urlCliente);
                    
                    // Envia a mensagem diretamente (removendo o teste estaAtivo que pode estar causando problema)
                    clienteRemoto.receberMensagemSala(remetente, mensagem);
                    System.out.println("✓ Mensagem enviada para @" + cliente.getApelido());
                    
                } catch (Exception e) {
                    System.out.println("⚠️ ERRO ao enviar para @" + cliente.getApelido() + ": " + e.getClass().getSimpleName() + " - " + e.getMessage());
                    // Mostrar mais detalhes do erro para debug
                    e.printStackTrace();
                }
            } else {
                System.out.println("Não enviando para o remetente @" + remetente);
            }
        }
        System.out.println("Broadcast finalizado");
    }
    
    /**
     * Verifica e remove clientes que estão realmente inativos
     * Este método deve ser chamado periodicamente ou quando houver suspeita de clientes órfãos
     */
    public void limparClientesInativos() throws RemoteException {
        List<String> clientesInativos = new ArrayList<>();
        
        System.out.println("Verificando conectividade dos clientes...");
        
        for (InfoCliente cliente : clientesAtivos.values()) {
            try {
                String urlCliente = "//" + cliente.getIp() + ":" + cliente.getPorta() + "/Cliente_" + cliente.getApelido();
                ClienteChatRemoto clienteRemoto = (ClienteChatRemoto) Naming.lookup(urlCliente);
                
                // Tenta fazer ping no cliente
                clienteRemoto.estaAtivo();
                System.out.println("✓ @" + cliente.getApelido() + " está ativo");
                
            } catch (Exception e) {
                System.out.println("✗ @" + cliente.getApelido() + " não está respondendo");
                clientesInativos.add(cliente.getApelido());
            }
        }
        
        // Remove apenas clientes que realmente não estão respondendo
        for (String apelido : clientesInativos) {
            removerCliente(apelido);
        }
        
        if (clientesInativos.size() > 0) {
            System.out.println("Removidos " + clientesInativos.size() + " clientes inativos");
        }
    }
    
    /**
     * Lista todos os clientes ativos (método adicional para administração)
     */
    public void mostrarClientesAtivos() {
        System.out.println("\n=== CLIENTES CONECTADOS ===");
        if (clientesAtivos.isEmpty()) {
            System.out.println("Nenhum cliente conectado");
        } else {
            for (InfoCliente cliente : clientesAtivos.values()) {
                System.out.println("  " + cliente);
            }
        }
        System.out.println("Total: " + clientesAtivos.size() + " clientes");
        System.out.println("==========================");
    }
    
    /**
     * Método principal para iniciar o servidor de chat
     */
    public static void main(String[] args) throws RemoteException, MalformedURLException {
        
        try {
            // Cria o registry RMI na porta 1099
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            System.out.println("RMI registry criado na porta 1099.");
        } catch (Exception e) {
            System.out.println("Registry RMI já existe ou erro ao criar:");
            e.printStackTrace();
        }
        
        // Cria e registra o servidor de chat
        ServidorChat servidorChat = new ServidorChat();
        Naming.rebind("ServidorChat", servidorChat);
        
        System.out.println("=== SERVIDOR DE CHAT RMI ===");
        System.out.println("Servidor registrado como 'ServidorChat'");
        System.out.println("Aguardando conexões de clientes...");
        System.out.println("============================");
        
        // Thread para comandos administrativos do servidor
        Thread threadComandos = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                try {
                    System.out.println("\nComandos do servidor:");
                    System.out.println("  'listar' - Mostrar clientes conectados");
                    System.out.println("  'limpar' - Verificar e remover clientes inativos");
                    System.out.println("  'sair' - Encerrar servidor");
                    System.out.print("servidor> ");
                    
                    String comando = scanner.nextLine().trim().toLowerCase();
                    
                    switch (comando) {
                        case "listar":
                            servidorChat.mostrarClientesAtivos();
                            break;
                        case "limpar":
                            servidorChat.limparClientesInativos();
                            break;
                        case "sair":
                            System.out.println("Encerrando servidor...");
                            scanner.close();
                            System.exit(0);
                            break;
                        default:
                            System.out.println("Comando inválido!");
                    }
                } catch (Exception e) {
                    System.out.println("Erro no comando: " + e.getMessage());
                }
            }
        });
        
        threadComandos.setDaemon(false);
        threadComandos.start();
    }
}
