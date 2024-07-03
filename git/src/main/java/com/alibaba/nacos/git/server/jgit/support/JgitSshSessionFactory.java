package com.alibaba.nacos.git.server.jgit.support;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.util.FS;

/**
 * TODO
 *
 * @author ysq
 * @date 2023/11/13 11:31
 */
public class JgitSshSessionFactory extends JschConfigSessionFactory {


    public JgitSshSessionFactory() {
    }

    @Override
    protected void configure(OpenSshConfig.Host host, Session session) {
        session.setConfig("StrictHostKeyChecking", "no");
    }

    @Override
    protected JSch createDefaultJSch(FS fs) throws JSchException {
        JSch sch = super.createDefaultJSch(fs);

        sch.addIdentity(privateKeyPath); //添加私钥文件

        //sch.addIdentity();

        return sch;
    }

}
