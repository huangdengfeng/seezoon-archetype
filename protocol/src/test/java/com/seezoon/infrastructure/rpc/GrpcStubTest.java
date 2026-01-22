package com.seezoon.infrastructure.rpc;

import com.seezoon.stub.greeter.GreeterGrpc;
import com.seezoon.stub.greeter.GreeterPb.HelloReply;
import com.seezoon.stub.greeter.GreeterPb.HelloRequest;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GrpcStubTest {

    @Autowired
    private GreeterGrpc.GreeterBlockingStub greeterBlockingStub;

    @Test
    void getGreeterStub() {
        try {
            HelloReply hello = greeterBlockingStub.sayHello(HelloRequest.newBuilder().setName("error").build());
            System.out.println(hello.getMessage());

        } catch (StatusRuntimeException e) {
            Metadata trailers = e.getTrailers();
            if (null != trailers) {
                // 读取 trailer
                String errorCode = trailers.get(
                        Metadata.Key.of("error_code", Metadata.ASCII_STRING_MARSHALLER)
                );
                String errorDetails = trailers.get(
                        Metadata.Key.of("detail", Metadata.ASCII_STRING_MARSHALLER)
                );
                System.out.println(errorCode + ":" + errorDetails);
            }

        }
    }
}