package zzk.webproject.air;

import org.apache.tomcat.websocket.WsSession;
import zzk.webproject.service.ChatMessageService;
import zzk.webproject.service.Services;
import zzk.webproject.service.Roster;
import zzk.webproject.util.StringUtil;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@ServerEndpoint(value = "/ws/chat", encoders = {AirMessageEncoder.class}, decoders = {AirMessageDecoder.class})
public class ChatEndpoint {
    private static final Logger LOGGER = Logger.getLogger(ChatEndpoint.class.getName());

    private static ChatMessageService chatMessageService = Services.getChatMessageService();

    private static final LinkedList<ChatEndpoint> ENDPOINTS = new LinkedList<>();
    private Session session;

    @OnOpen
    public void open(Session session) {
        this.session = session;
        registerEndpoint(this);

        String username = getUsername(session);
        AirMessage message = new AirMessage();
        message.setType(MessageType.SYSTEM_MESSAGE);
        message.setContent("online");
        message.setFromAccount(username);
        broadcast(message);
        recordMessage(message);

        transferUnacceptedMessage(session);
        transferOnlineFriend(session);

        LOGGER.log(Level.INFO, String.format("用户%s连接到了websocket", username));
    }

    @OnClose
    public void end() {
        String username = getUsername(session);
        AirMessage message = new AirMessage();
        message.setType(MessageType.SYSTEM_MESSAGE);
        message.setContent("offline");
        message.setFromAccount(username);
        broadcast(message);
        recordMessage(message);

        unregisterEndpoint(this);
        session = null;
        LOGGER.log(Level.INFO, String.format("用户%s从websocket断开", username));
    }

    @OnMessage
    public void message(AirMessage airMessage) {
        airMessage.setHappenDataTime(LocalDateTime.now());
        String toAccount = airMessage.getToAccount();
        recordMessage(airMessage);
        if (!airMessage.getBroadcastMessage() && StringUtil.nonBlank(toAccount)) {
            ChatEndpoint endpoint = getEndPointByUsername(toAccount);
            sendObject(endpoint, airMessage);
        } else {
            broadcast(airMessage);
        }
        LOGGER.log(Level.INFO, airMessage.toString());
    }

    @OnError
    public void error(Throwable throwable) {
        throwable.printStackTrace();
        end();
        LOGGER.log(Level.SEVERE, throwable.getMessage());
    }

    /**
     * 从wssession获取用户名
     *
     * @param session
     * @return
     */
    private String getUsername(Session session) {
        String httpSessionId = ((WsSession) session).getHttpSessionId();
        return Roster.getUsername(httpSessionId);
    }

    private ChatEndpoint getEndPointByUsername(String username) {
        Optional<ChatEndpoint> chatEndpoint = ENDPOINTS.stream()
                .filter(endpoint -> username.equals(getUsername(endpoint.session)))
                .findFirst();
        return chatEndpoint.get();
    }

    /**
     * 记录连接到websocket的终端
     *
     * @param endpoint
     */
    private void registerEndpoint(ChatEndpoint endpoint) {
        ENDPOINTS.add(endpoint);
    }

    /**
     * 删除终端
     *
     * @param endpoint
     */
    private void unregisterEndpoint(ChatEndpoint endpoint) {
        ENDPOINTS.remove(endpoint);
    }

    private void sendObject(ChatEndpoint endpoint, Object message) {
        try {
            endpoint.session.getBasicRemote().sendObject(message);
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        }
    }

    /**
     * 广播Object类型的信息，不过格式会是Json的
     *
     * @param object
     */
    private void broadcast(Object object) {
        broadcast(object, this);
    }

    /**
     * 向除了@param exclude的所有终端发送Object信息
     *
     * @param object
     * @param exclude
     */
    private void broadcast(Object object, ChatEndpoint exclude) {
        for (ChatEndpoint endpoint : ENDPOINTS) {
            if (exclude == endpoint) {
                continue;
            }
            sendObject(endpoint, object);
        }
    }

    /**
     * 记录接受的信息
     * 用于推送历史信息
     *
     * @param message
     */
    private void recordMessage(AirMessage message) {
        chatMessageService.save(message);
    }

    /**
     * 推送历史信息
     *
     * @param session
     */
    private void transferUnacceptedMessage(Session session) {
        RemoteEndpoint.Basic basicRemote = session.getBasicRemote();
        chatMessageService.list(airMessage -> {
                    boolean isShortMessage;
                    boolean isLongText;
                    isShortMessage = MessageType.SHORT_MESSAGE.name().equals(airMessage.getType());
                    isLongText = MessageType.REFERENCE.name().equals(airMessage.getType());
                    return isLongText || isShortMessage;
                }
        ).forEach(airMessage -> {
            try {
                basicRemote.sendObject(airMessage);
            } catch (IOException | EncodeException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 推送在线的用户
     *
     * @param session
     */
    private void transferOnlineFriend(Session session) {
        RemoteEndpoint.Basic basicRemote = session.getBasicRemote();
        for (ChatEndpoint endpoint : ENDPOINTS) {
            try {
                Session currentSession = endpoint.session;
                if (currentSession == session) {
                    continue;
                }
                AirMessage airMessage = new AirMessage("online");
                airMessage.setType(MessageType.SYSTEM_MESSAGE);
                airMessage.setFromAccount(getUsername(currentSession));
                basicRemote.sendObject(airMessage);
            } catch (EncodeException | IOException e) {
                LOGGER.severe(e.getMessage());
            }
        }

    }
}

