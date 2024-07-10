package core;

import core.entity.Model;
import core.utils.Utils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ObjectLoader {
    private List<Integer> vaos = new ArrayList<Integer>();
    private List<Integer> vbos = new ArrayList<Integer>();
    private List<Integer> textures = new ArrayList<Integer>();

    public Model loadModel(float[] vertices, float[] textureCoords, int[] indices) {
        int id = createVAO();
        storeIndicesBuffer(indices);
        storeDataInAttributeList(0,3, vertices);
        storeDataInAttributeList(1,2, textureCoords);
        unbind();
        return new Model(id, indices.length);
    }

    public int loadTexture(String filename) throws Exception {
        int width, height;
        ByteBuffer buffer;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            buffer = STBImage.stbi_load(filename, w, h, c, 4);
            if (buffer == null)
                throw new Exception("Failed to load texture: " + filename + STBImage.stbi_failure_reason());

            width = w.get();
            height = h.get();
        }

        int id = GL30.glGenTextures();
        textures.add(id);
        GL11.glBindTexture(GL30.GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL30.GL_TEXTURE_2D, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);
        return id;
    }

    private int createVAO() {
        int id = GL30.glGenVertexArrays();
        vaos.add(id);
        GL30.glBindVertexArray(id);
        return id;
    }

    private void storeIndicesBuffer(int[] indices) {
        int vbo = GL30.glGenBuffers();
        vbos.add(vbo);
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.storeDataInIntBuffer(indices);
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, buffer, GL30.GL_STATIC_DRAW);
    }

    private void storeDataInAttributeList(int attribNo, int vertexCount, float[] data) {
        int vbo = GL30.glGenBuffers();
        vbos.add(vbo);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = Utils.storeDataInFloatBuffer(data);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, buffer, GL30.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(attribNo, vertexCount, GL30.GL_FLOAT, false, 0, 0);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
    }

    private void unbind() {
        GL30.glBindVertexArray(0);
    }

    public void cleanup() {
        for(int vao : vaos) {
            GL30.glDeleteVertexArrays(vao);
        }
        for(int vbo : vbos) {
            GL30.glDeleteBuffers(vbo);
        }
        for(int texture : textures) {
            GL11.glDeleteTextures(texture);
        }
    }
}
