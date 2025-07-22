import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;


public class Servidor extends UnicastRemoteObject implements ServidorRemoto {

	public Servidor() throws RemoteException {}
	
	public void escreveMsg (String msg)throws RemoteException{
		System.out.println(msg);
	}
	
	public Date dataDeHoje() throws RemoteException{
		return new Date();
	}
	public static void main(String[] args) throws RemoteException, MalformedURLException{
		
		// In modern Java, we don't need to set a security manager for basic RMI operations
		// if (System.getSecurityManager() == null){
		//	System.setSecurityManager(new RMISecurityManager());
		// }
		
		try {      
			  //Fazer o registro para a porta desejado
			  java.rmi.registry.LocateRegistry.createRegistry(1099);
			  System.out.println("RMI registry ready.");
			} catch (Exception e) {
			  System.out.println("Exception starting RMI registry:");
			  e.printStackTrace();
			}
				
		Servidor servidor = new Servidor();
		
		Naming.rebind("Servidor", servidor);
		System.out.println("Servidor RMI iniciado e registrado!");
	}

}
