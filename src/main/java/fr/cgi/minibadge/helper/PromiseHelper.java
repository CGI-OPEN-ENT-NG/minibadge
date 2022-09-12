package fr.cgi.minibadge.helper;

import fr.cgi.minibadge.core.constants.Request;
import fr.wseduc.webutils.Either;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class PromiseHelper {
    private static final Logger log = LoggerFactory.getLogger(PromiseHelper.class);

    private PromiseHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static <R> Handler<Either<String, R>> handler(Promise<R> promise) {
        return handler(promise, null);
    }

    public static <R> Handler<Either<String, R>> handler(Promise<R> promise, String errorMessage) {
        return event -> {
            if (event.isRight()) {
                promise.complete(event.right().getValue());
                return;
            }
            log.error(String.format("%s %s", (errorMessage != null ? errorMessage : ""), event.left().getValue()));
            promise.fail(errorMessage != null ? errorMessage : event.left().getValue());
        };
    }

    public static <R> Handler<AsyncResult<Message<JsonObject>>> messageHandler(Promise<R> promise) {
        return messageHandler(promise, null);
    }

    @SuppressWarnings("unchecked")
    public static <R> Handler<AsyncResult<Message<JsonObject>>> messageHandler(Promise<R> promise, String errorMessage) {
        return event -> {
            if (event.succeeded() && Request.OK.equals(event.result().body().getString(Request.STATUS)))
                promise.complete((R) event.result().body().getValue(Request.RESULT,
                        event.result().body().getValue(Request.RESULTS)));
            else {
                boolean isErrorMessageDefined = errorMessage != null;
                String errorMessageValue = (isErrorMessageDefined ? errorMessage : "");
                if (event.failed()) {
                    log.error(String.format("%s %s", errorMessageValue, event.cause().getMessage()));
                    promise.fail(isErrorMessageDefined ? errorMessage : event.cause().getMessage());
                    return;
                }
                log.error(String.format("%s %s", errorMessageValue,
                        event.result().body().getString(Request.MESSAGE)));
                promise.fail(isErrorMessageDefined ? errorMessage : event.result().body().getString(Request.MESSAGE));
            }
        };
    }
}
