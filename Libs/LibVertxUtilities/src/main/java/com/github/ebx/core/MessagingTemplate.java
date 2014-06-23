package com.github.ebx.core;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.Map;

/**
 * @author Gregory Boissinot
 */
public class MessagingTemplate {

    private MessagingTemplate() {
    }

    public static InternalMessagingTemplate address(EventBus eventBus, String address) {
        return new InternalMessagingTemplate(eventBus, address);
    }

    public static class InternalMessagingTemplate {

        private static final long DEFAULTVALUE_TIMOUT = 10 * 1000L; //10 seconds

        private static final String KEY_ACTION = "action";
        private static final String KEY_CONTENT = "content";

        private final String address;
        private final EventBus eventBus;
        private String action;
        private Object content;
        private long timeout;

        public InternalMessagingTemplate(EventBus eventBus, String address) {
            this.eventBus = eventBus;
            this.address = address;
        }

        public InternalMessagingTemplate action(String action) {
            this.action = action;
            return this;
        }

        public InternalMessagingTemplate content(JsonObject content) {
            this.content = content;
            return this;
        }

        public InternalMessagingTemplate content(JsonArray content) {
            this.content = content;
            return this;
        }

        public InternalMessagingTemplate content(String content) {
            this.content = content;
            return this;
        }

        public InternalMessagingTemplate content(Number content) {
            this.content = content;
            return this;
        }

        public InternalMessagingTemplate content(Boolean content) {
            this.content = content;
            return this;
        }

        public InternalMessagingTemplate content(byte[] content) {
            this.content = content;
            return this;
        }

        public InternalMessagingTemplate content(Character content) {
            this.content = content;
            return this;
        }

        public InternalMessagingTemplate content(Object content) {
            //TODO Serialize Object into JSON
            this.content = content;
            return this;
        }

        public InternalMessagingTemplate timeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        public InternalMessagingTemplate message(String action, Object content) {
            this.action = action;
            return content(content);
        }

        public <R> void send(Handler<AsyncResult<Message<R>>> replyHandler) {
            if (timeout == 0) {
                timeout = DEFAULTVALUE_TIMOUT;
            }
            eventBus.sendWithTimeout(address, getEventBusMessage(), timeout, replyHandler);
        }

        private JsonObject getEventBusMessage() {
            JsonObject sendObject = new JsonObject();
            sendObject.putString(KEY_ACTION, action);

            //TODO Refactor
            if (content instanceof JsonObject) {
                JsonObject jsonObject = (JsonObject) content;
                final Map<String, Object> jsonObjectMap = jsonObject.toMap();
                for (Map.Entry<String, Object> entry : jsonObjectMap.entrySet()) {
                    sendObject.putValue(entry.getKey(), entry.getValue());
                }
                return sendObject;
            }

            sendObject.putValue(KEY_CONTENT, content);
            return sendObject;
        }
    }


}
