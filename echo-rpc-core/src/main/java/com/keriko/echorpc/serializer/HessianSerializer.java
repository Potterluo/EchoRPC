package com.keriko.echorpc.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Hessian 序列化器
 *
 */
@Slf4j
public class HessianSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T object) throws IOException {
//        log.info("HessianSerializer serialize: {}", object);
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            HessianOutput ho = new HessianOutput(bos);
            ho.writeObject(object);
            return bos.toByteArray();
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> tClass) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
            HessianInput hi = new HessianInput(bis);
            Object obj = hi.readObject();
            if (!tClass.isInstance(obj)) {
                throw new IOException("Deserialized object is not an instance of " + tClass.getName());
            }
            return tClass.cast(obj);
        }
    }
}
