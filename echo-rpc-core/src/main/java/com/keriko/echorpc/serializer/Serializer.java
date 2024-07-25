package com.keriko.echorpc.serializer;

import java.io.IOException;

/**
 * 序列化器接口
 */
public interface Serializer {

    /**
     * Serializes an object into a byte array.
     * <p>
     * This method provides a generic serialization mechanism, allowing any object that implements Serializable to be converted into a byte array for storage or transmission.
     * The specific implementation may use various serialization techniques, such as Java's native serialization or custom serialization algorithms.
     *
     * @param object The object to be serialized, must implement Serializable interface.
     * @param <T> The type parameter indicating the type of the object.
     * @return A byte array representing the serialized object.
     * @throws IOException If the serialization process encounters an I/O error.
     */
    <T> byte[] serialize(T object) throws IOException;

    /**
     * Deserializes a byte array into an object.
     * <p>
     * This method provides a generic deserialization mechanism, taking a byte array and a class reference as input, and returns an instance of the specified class.
     * The byte array should have been serialized using the corresponding to serialize method, and the deserialization process needs to adhere to the same serialization protocol.
     *
     * @param bytes The byte array representing the serialized object.
     * @param type The class reference indicating the type of object to be deserialized.
     * @param <T> The type parameter indicating the type of the object.
     * @return An instance of the specified class, representing the deserialized object.
     * @throws IOException If the deserialization process encounters an I/O error.
     */
    <T> T deserialize(byte[] bytes, Class<T> type) throws IOException;
}
