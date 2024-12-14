///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS info.picocli:picocli:4.6.3
//DEPS info.picocli:picocli-codegen:4.6.3
//DEPS com.ibm.mq:com.ibm.mq.allclient:9.4.1.0


import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;

@Command(name = "ibmmqsdr", mixinStandardHelpOptions = true, version = "ibmmqsdr 0.1",
        description = "IBM MQ message sender")
class ibmmqsdr implements Callable<Integer> {

    @Parameters(index = "0", description = "queue name", defaultValue = "DEV.QUEUE.1")
    private String queue;

    @Option(names = {"-h", "--host"}, description = "hostname")
    private String host = "localhost";

    @Option(names = {"-p", "--port"}, description = "port")
    private Integer port = 1414;

    @Option(names = {"-m", "--queue-manager"}, description = "queue manager name")
    private String queueManager = "QM1";

    @Option(names = {"-c", "--channel"}, description = "channel name")
    private String channel = "DEV.APP.SVRCONN";

    @Option(names = {"--user"}, description = "user name")
    private String user;

    @Option(names = {"--password"}, description = "password")
    private String password;

    public static void main(String... args) {
        int exitCode = new CommandLine(new ibmmqsdr()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
        JmsConnectionFactory cf = ff.createConnectionFactory();
        cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, host);
        cf.setIntProperty(WMQConstants.WMQ_PORT, port);
        cf.setStringProperty(WMQConstants.WMQ_CHANNEL, channel);
        cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
        cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, queueManager);
        if (user != null) {
            cf.setStringProperty(WMQConstants.USERID, user);
        }
        if (password != null) {
            cf.setStringProperty(WMQConstants.PASSWORD, password);
        }
        Connection connection = cf.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(queue);
        MessageProducer producer = session.createProducer(destination);
        TextMessage message = session.createTextMessage("Hello from JBang");
        producer.send(message);
        producer.close();
        session.close();
        connection.close();
        return 0;
    }
}