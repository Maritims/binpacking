package no.clueless.binpacking.visualization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import no.clueless.binpacking.shared.NonEmptyList;
import no.clueless.binpacking.three_dimensional.Bin3D;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class JettyVisualizer {
    private static final Logger                             log        = LoggerFactory.getLogger(JettyVisualizer.class);
    private static final CopyOnWriteArrayList<AsyncContext> sseClients = new CopyOnWriteArrayList<>();

    private static String currentPayload = "[]";

    private static void broadcast(String payload) {
        log.info("Broadcast triggered! Pushing to {} active clients", sseClients.size());
        for (var context : sseClients) {
            try {
                var writer = context.getResponse().getWriter();
                writer.print("data:" + payload + "\r\n\r\n");
                writer.flush();
            } catch (IOException e) {
                context.complete();
                sseClients.remove(context);
            }
        }
    }

    public static void preview(NonEmptyList<Bin3D> bins) throws JsonProcessingException {
        Objects.requireNonNull(bins, "bins cannot be null");

        var port         = 8081;
        var server       = new Server(port);
        var objectMapper = new ObjectMapper();
        var json         = objectMapper.writeValueAsString(bins);

        var dataApiHandler = new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
                if ("/2d.json".equals(target)) {
                    baseRequest.setHandled(true);
                    response.setContentType("application/json; charset=UTF-8");
                    response.setStatus(200);
                    try (var writer = response.getWriter()) {
                        writer.write(json);
                    }
                } else if ("/stream".equals(target)) {
                    baseRequest.setHandled(true);

                    response.setContentType("text/event-stream");
                    response.setCharacterEncoding("UTF-8");
                    response.setHeader("Cache-Control", "no-cache");
                    response.setHeader("Connection", "keep-alive");
                    response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
                    response.setStatus(200);

                    var asyncContext = request.startAsync();
                    asyncContext.setTimeout(0);
                    sseClients.add(asyncContext);

                    var writer = response.getWriter();
                    writer.print("data:" + currentPayload + "\r\n\r\n");
                    writer.flush();
                } else if ("/broadcast".equals(target) && "POST".equals(request.getMethod())) {
                    baseRequest.setHandled(true);

                    var body = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                    if (body.isBlank()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        return;
                    }

                    currentPayload = body;
                    response.setStatus(200);

                    broadcast(currentPayload);
                }
            }
        };

        var handlers = new HandlerList();
        //handlers.addHandler(resourceHandler);
        handlers.addHandler(dataApiHandler);

        server.setHandler(handlers);

        try {
            server.start();
            log.info("Jetty Visualization Engine listening at http://localhost:{}", port);

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI("http://localhost:" + port));
            }

            server.join();
        } catch (Exception e) {
            log.error("Failed to start Jetty Visualization Engine: {}", e.getMessage(), e);
        }
    }
}
