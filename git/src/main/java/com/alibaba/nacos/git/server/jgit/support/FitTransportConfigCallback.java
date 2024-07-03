package com.alibaba.nacos.git.server.jgit.support;

import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.transport.HttpTransport;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;

/**
 * Automatic fit TransportConfigCallback.
 *
 * @author ysq
 * @date 2023/11/13 11:02
 */
public class FitTransportConfigCallback implements TransportConfigCallback {

    @Override
    public void configure(Transport transport) {
        if (transport instanceof SshTransport) {
            // 如果需要进行 SSH 配置，可以在这里添加相关配置

            SshTransport sshTransport = (SshTransport) transport;

            JgitSshSessionFactory jgitSshSessionFactory = new JgitSshSessionFactory();

            //sshTransport.setCredentialsProvider(new CustomCredentialProvider(passphrase));

            //sshTransport.setSshSessionFactory(new SshdSessionFactory());
            sshTransport.setSshSessionFactory(jgitSshSessionFactory);


            System.out.println("ssh transport ");

        } else if (transport instanceof HttpTransport) {
            // 如果使用 HTTP 协议，可以在这里添加相关配置
            System.out.println("http transport ");
        } else {
            throw new RuntimeException("Protocol does not support yet ! ");
        }
    }

}
