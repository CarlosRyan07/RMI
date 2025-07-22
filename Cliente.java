import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Date;


public class Cliente {

	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
		
		String host = "127.0.0.1";
		
		if ( args.length == 1){
			host = args[0];
		}
		
		// In modern Java, we don't need to set a security manager for basic RMI operations
		// if (System.getSecurityManager() == null){
		//	System.setSecurityManager(new RMISecurityManager());
		// }
		
		String nomeRemoto = "//" + host + "/Servidor";
		
		ServidorRemoto servidor = (ServidorRemoto) Naming.lookup(nomeRemoto);
		
		//escreve mensagem no servidor, chamando método dele
		servidor.escreveMsg("Hello, fellows!!!!");
		
		//recebe a data de hoje do servidor, executando método lá nele
		Date dataDeHoje = servidor.dataDeHoje();
		System.out.println("A data/hora do servidor é: " + dataDeHoje.toString());
	}

}
