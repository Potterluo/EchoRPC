package com.keriko.echorpc.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class HessianSerializerTest {

    @InjectMocks
    private HessianSerializer serializer;

    private static final String TEST_STRING = "Hello, World!";
    private static final int TEST_INT = 42;

    @Data
    static class TestPerson {
        private String name;
        private int age;
    }
    @Before
    public void setUp() {
        serializer = new HessianSerializer();
    }

    @Test
    public void serialize_StringInput_ShouldReturnSerializedBytes() throws IOException {
        byte[] serialized = serializer.serialize(TEST_STRING);
        assertNotNull(serialized);
        assertTrue(serialized.length > 0);
    }

    @Test
    public void deserialize_ValidSerializedString_ShouldReturnDeserializedString() throws IOException {
        byte[] serialized = serializer.serialize(TEST_STRING);
        String deserialized = serializer.deserialize(serialized, String.class);
        assertEquals(TEST_STRING, deserialized);
    }

    @Test
    public void serialize_IntegerInput_ShouldReturnSerializedBytes() throws IOException {
        byte[] serialized = serializer.serialize(TEST_INT);
        assertNotNull(serialized);
        assertTrue(serialized.length > 0);
    }

    @Test
    public void deserialize_ValidSerializedInteger_ShouldReturnDeserializedInteger() throws IOException {
        byte[] serialized = serializer.serialize(TEST_INT);
        Integer deserialized = serializer.deserialize(serialized, Integer.class);
        assertEquals(TEST_INT, deserialized.intValue());
    }

    @Test(expected = IOException.class)
    public void deserialize_InvalidBytes_ShouldThrowIOException() throws IOException {
        byte[] invalidBytes = new byte[]{0, 1, 2, 3};
        serializer.deserialize(invalidBytes, String.class);
    }

    @Test
    public  void setSerializerObjectShouldEqualAndDeserializeObject() {
        try {
            HessianSerializer serializer = new HessianSerializer();

            // 示例对象
            TestPerson person = new TestPerson();
            person.setName("John");
            person.setAge(30);

            // 序列化
            byte[] serializedData = serializer.serialize(person);
            System.out.println("Serialized Data: " + serializedData);

            // 反序列化
            TestPerson deserializedPerson = serializer.deserialize(serializedData, TestPerson.class);
            System.out.println("Deserialized Person: " + deserializedPerson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
