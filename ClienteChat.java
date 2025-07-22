import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;

/**
 * Cliente de chat RMI
 * Implementa tanto a interface de usuário quanto o servidor P2P para receber mensagens
 */
public class ClienteChat extends UnicastRemoteObject implements ClienteChatRemoto {
    private static final long serialVersionUID = 1L;
    
    private String meuApelido;
    private String meuIp;
    private int minhaPorta;
    private ServidorChatRemoto servidorChat;
    private Scanner scanner;
    
    public ClienteChat(String apelido, String ip, int porta) throws RemoteException {
        super();
        this.meuApelido = apelido;
        this.meuIp = ip;
        this.minhaPorta = porta;
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Conecta ao servidor de chat central
     */
    public boolean conectarAoServidor(String hostServidor) {
        try {
            String urlServidor = "//" + hostServidor + "/ServidorChat";
            servidorChat = (ServidorChatRemoto) Naming.lookup(urlServidor);
            
            // Registra este cliente no servidor
            boolean sucesso = servidorChat.registrarCliente(meuApelido, meuIp, minhaPorta);
            
            if (sucesso) {
                System.out.println("✓ Conectado ao servidor de chat como @" + meuApelido);
                return true;
            } else {
                System.out.println("✗ Falha ao registrar - @ já existe: " + meuApelido);
                return false;
            }
            
        } catch (Exception e) {
            System.out.println("✗ Erro ao conectar ao servidor: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Registra este cliente como servidor RMI para receber mensagens P2P
     */
    public void iniciarServidorP2P() {
        try {
            // Primeiro, tenta criar registry local para este cliente
            try {
                java.rmi.registry.LocateRegistry.createRegistry(minhaPorta);
                System.out.println("✓ Registry criado na porta " + minhaPorta);
            } catch (Exception e) {
                System.out.println("⚠️ Registry já existe na porta " + minhaPorta + " ou erro: " + e.getMessage());
            }
            
            // Registra este cliente no registry
            String nomeServico = "Cliente_" + meuApelido;
            Naming.rebind("//" + meuIp + ":" + minhaPorta + "/" + nomeServico, this);
            
            System.out.println("✓ Servidor P2P iniciado:");
            System.out.println("  - IP: " + meuIp);
            System.out.println("  - Porta: " + minhaPorta);
            System.out.println("  - Serviço: " + nomeServico);
            System.out.println("  - URL completa: //" + meuIp + ":" + minhaPorta + "/" + nomeServico);
            
            // Teste se consegue fazer lookup de si mesmo
            try {
                ClienteChatRemoto teste = (ClienteChatRemoto) Naming.lookup("//" + meuIp + ":" + minhaPorta + "/" + nomeServico);
                boolean ativo = teste.estaAtivo();
                System.out.println("✓ Auto-teste bem-sucedido: " + ativo);
            } catch (Exception e) {
                System.out.println("⚠️ Falha no auto-teste: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("✗ Erro ao iniciar servidor P2P: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Implementação da interface ClienteChatRemoto
    
    @Override
    public void receberMensagem(String remetente, String mensagem) throws RemoteException {
        System.out.println("\n[MENSAGEM PRIVADA] @" + remetente + ": " + mensagem);
        System.out.print(">> ");
    }
    
    @Override
    public void receberMensagemSala(String remetente, String mensagem) throws RemoteException {
        System.out.println("\n[SALA] @" + remetente + ": " + mensagem);
        System.out.print(">> ");
    }
    
    @Override
    public void receberArquivo(String remetente, String nomeArquivo, byte[] conteudo) throws RemoteException {
        try {
            // Salva o arquivo recebido
            File arquivo = new File("recebido_" + nomeArquivo);
            FileOutputStream fos = new FileOutputStream(arquivo);
            fos.write(conteudo);
            fos.close();
            
            System.out.println("\n[ARQUIVO] Recebido de @" + remetente + ": " + nomeArquivo + 
                             " (salvo como: " + arquivo.getName() + ")");
            System.out.print(">> ");
            
        } catch (IOException e) {
            System.out.println("\n✗ Erro ao salvar arquivo de @" + remetente + ": " + e.getMessage());
            System.out.print(">> ");
        }
    }
    
    @Override
    public boolean estaAtivo() throws RemoteException {
        return true;
    }
    
    // Métodos para enviar mensagens
    
    /**
     * Envia mensagem privada para outro cliente
     */
    public void enviarMensagemPrivada(String destinatario, String mensagem) {
        try {
            System.out.println("Buscando cliente @" + destinatario + " no servidor...");
            InfoCliente infoDestinatario = servidorChat.buscarCliente(destinatario);
            
            if (infoDestinatario == null) {
                System.out.println("✗ Cliente @" + destinatario + " não encontrado no servidor!");
                return;
            }
            
            System.out.println("✓ Cliente encontrado: " + infoDestinatario);
            
            // Conecta diretamente ao cliente (P2P)
            String urlCliente = "//" + infoDestinatario.getIp() + ":" + 
                               infoDestinatario.getPorta() + "/Cliente_" + destinatario;
            System.out.println("Conectando a: " + urlCliente);
            
            ClienteChatRemoto clienteDestino = (ClienteChatRemoto) Naming.lookup(urlCliente);
            
            // Envia a mensagem
            clienteDestino.receberMensagem(meuApelido, mensagem);
            System.out.println("✓ Mensagem enviada para @" + destinatario);
            
        } catch (Exception e) {
            System.out.println("✗ Erro ao enviar mensagem para @" + destinatario + ": " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Envia mensagem para a sala (broadcast)
     */
    public void enviarMensagemSala(String mensagem) {
        try {
            servidorChat.enviarMensagemSala(meuApelido, mensagem);
            System.out.println("✓ Mensagem enviada para a sala");
        } catch (Exception e) {
            System.out.println("✗ Erro ao enviar mensagem para a sala: " + e.getMessage());
        }
    }
    
    /**
     * Envia arquivo para outro cliente
     */
    public void enviarArquivo(String destinatario, String caminhoArquivo) {
        try {
            InfoCliente infoDestinatario = servidorChat.buscarCliente(destinatario);
            
            if (infoDestinatario == null) {
                System.out.println("✗ Cliente @" + destinatario + " não encontrado!");
                return;
            }
            
            // Lê o arquivo
            File arquivo = new File(caminhoArquivo);
            if (!arquivo.exists()) {
                System.out.println("✗ Arquivo não encontrado: " + caminhoArquivo);
                return;
            }
            
            FileInputStream fis = new FileInputStream(arquivo);
            byte[] conteudo = fis.readAllBytes();
            fis.close();
            
            // Conecta ao cliente e envia o arquivo
            String urlCliente = "//" + infoDestinatario.getIp() + ":" + 
                               infoDestinatario.getPorta() + "/Cliente_" + destinatario;
            ClienteChatRemoto clienteDestino = (ClienteChatRemoto) Naming.lookup(urlCliente);
            
            clienteDestino.receberArquivo(meuApelido, arquivo.getName(), conteudo);
            System.out.println("✓ Arquivo enviado para @" + destinatario + ": " + arquivo.getName());
            
        } catch (Exception e) {
            System.out.println("✗ Erro ao enviar arquivo: " + e.getMessage());
        }
    }
    
    /**
     * Lista todos os clientes ativos
     */
    public void listarClientes() {
        try {
            List<InfoCliente> clientes = servidorChat.listarClientesAtivos();
            
            if (clientes.isEmpty()) {
                System.out.println("Nenhum outro cliente conectado.");
            } else {
                System.out.println("\n=== CLIENTES ATIVOS ===");
                for (InfoCliente cliente : clientes) {
                    if (!cliente.getApelido().equals(meuApelido)) {
                        System.out.println("  " + cliente);
                    }
                }
                System.out.println("====================");
            }
        } catch (Exception e) {
            System.out.println("✗ Erro ao listar clientes: " + e.getMessage());
        }
    }
    
    /**
     * Mostra informações de debug do cliente
     */
    public void mostrarDebug() {
        System.out.println("\n=== DEBUG DO CLIENTE ===");
        System.out.println("Meu @: " + meuApelido);
        System.out.println("Meu IP: " + meuIp);
        System.out.println("Minha Porta: " + minhaPorta);
        System.out.println("URL do meu serviço: //" + meuIp + ":" + minhaPorta + "/Cliente_" + meuApelido);
        
        // Teste de auto-conectividade
        try {
            ClienteChatRemoto teste = (ClienteChatRemoto) Naming.lookup("//" + meuIp + ":" + minhaPorta + "/Cliente_" + meuApelido);
            boolean ativo = teste.estaAtivo();
            System.out.println("Auto-teste: " + (ativo ? "✓ FUNCIONANDO" : "✗ FALHA"));
        } catch (Exception e) {
            System.out.println("Auto-teste: ✗ FALHA - " + e.getMessage());
        }
        
        // Teste de conectividade com servidor
        try {
            List<InfoCliente> clientes = servidorChat.listarClientesAtivos();
            System.out.println("Conectividade com servidor: ✓ OK (" + clientes.size() + " clientes)");
        } catch (Exception e) {
            System.out.println("Conectividade com servidor: ✗ FALHA - " + e.getMessage());
        }
        
        System.out.println("=======================");
    }
    
    /**
     * Desconecta do servidor de chat
     */
    public void desconectar() {
        try {
            if (servidorChat != null) {
                servidorChat.removerCliente(meuApelido);
                System.out.println("✓ Desconectado do servidor de chat");
            }
        } catch (Exception e) {
            System.out.println("Erro ao desconectar: " + e.getMessage());
        }
    }
    
    /**
     * Interface de usuário do chat
     */
    public void iniciarChat() {
        System.out.println("\n=== CHAT RMI - @" + meuApelido + " ===");
        System.out.println("Comandos disponíveis:");
        System.out.println("  /msg @usuario mensagem     - Enviar mensagem privada");
        System.out.println("  /sala mensagem             - Enviar mensagem para sala");
        System.out.println("  /arquivo @usuario caminho  - Enviar arquivo");
        System.out.println("  /listar                    - Listar clientes ativos");
        System.out.println("  /debug                     - Mostrar informações de debug");
        System.out.println("  /sair                      - Sair do chat");
        System.out.println("================================");
        
        while (true) {
            System.out.print(">> ");
            String entrada = scanner.nextLine().trim();
            
            if (entrada.isEmpty()) continue;
            
            if (entrada.equals("/sair")) {
                break;
            } else if (entrada.equals("/listar")) {
                listarClientes();
            } else if (entrada.equals("/debug")) {
                mostrarDebug();
            } else if (entrada.startsWith("/msg ")) {
                processarMensagemPrivada(entrada);
            } else if (entrada.startsWith("/sala ")) {
                String mensagem = entrada.substring(6);
                enviarMensagemSala(mensagem);
            } else if (entrada.startsWith("/arquivo ")) {
                processarEnvioArquivo(entrada);
            } else {
                System.out.println("Comando inválido. Digite /sair para sair.");
            }
        }
        
        desconectar();
        System.out.println("Chat encerrado!");
    }
    
    private void processarMensagemPrivada(String entrada) {
        String[] partes = entrada.split(" ", 3);
        if (partes.length < 3) {
            System.out.println("Uso: /msg @usuario mensagem");
            return;
        }
        
        String destinatario = partes[1].startsWith("@") ? partes[1].substring(1) : partes[1];
        String mensagem = partes[2];
        
        enviarMensagemPrivada(destinatario, mensagem);
    }
    
    private void processarEnvioArquivo(String entrada) {
        String[] partes = entrada.split(" ", 3);
        if (partes.length < 3) {
            System.out.println("Uso: /arquivo @usuario caminho_do_arquivo");
            return;
        }
        
        String destinatario = partes[1].startsWith("@") ? partes[1].substring(1) : partes[1];
        String caminhoArquivo = partes[2];
        
        enviarArquivo(destinatario, caminhoArquivo);
    }
    
    /**
     * Método principal
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ClienteChat cliente = null;
        
        try {
            System.out.println("=== CLIENTE DE CHAT RMI ===");
            
            // Solicita informações do usuário
            System.out.print("Digite seu @ (apelido único): ");
            String apelido = scanner.nextLine().trim();
            
            if (apelido.startsWith("@")) {
                apelido = apelido.substring(1);
            }
            
            // Obtém IP local automaticamente
            String ip = InetAddress.getLocalHost().getHostAddress();
            
            System.out.print("Digite a porta para P2P (ex: 1100): ");
            int porta = Integer.parseInt(scanner.nextLine().trim());
            
            System.out.print("IP do servidor de chat (Enter para localhost): ");
            String hostServidor = scanner.nextLine().trim();
            if (hostServidor.isEmpty()) {
                hostServidor = "127.0.0.1";
            }
            
            // Cria e configura o cliente
            cliente = new ClienteChat(apelido, ip, porta);
            
            // Adiciona shutdown hook para desconexão limpa
            final ClienteChat clienteFinal = cliente;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nDesconectando...");
                clienteFinal.desconectar();
            }));
            
            // Inicia servidor P2P
            cliente.iniciarServidorP2P();
            
            // Conecta ao servidor central
            if (cliente.conectarAoServidor(hostServidor)) {
                // Inicia a interface do chat
                cliente.iniciarChat();
            }
            
        } catch (Exception e) {
            System.out.println("✗ Erro: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
            if (cliente != null) {
                cliente.desconectar();
            }
        }
    }
}
