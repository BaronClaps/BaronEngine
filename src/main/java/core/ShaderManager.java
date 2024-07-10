package core;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.INTELBlackholeRender;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;

public class ShaderManager {
    private final int programId;
    private int vertexShaderID, fragmentShaderID;

    private final Map<String, Integer> uniforms;

    public ShaderManager() throws Exception {
        programId = GL30.glCreateProgram();
        if (programId == 0)
            throw new Exception("Cannot create shader");

        uniforms = new HashMap<>();
    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = GL30.glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0)
            throw new Exception("Cannot find uniform " + uniformName);
        uniforms.put(uniformName, uniformLocation);
    }

    public void setUniforms(String uniformName, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GL30.glUniformMatrix4fv(uniforms.get(uniformName), false, value.get(stack.mallocFloat(16)));
        }
    }

    public void setUniforms(String uniformName, int value) {
        GL30.glUniform1i(uniforms.get(uniformName), value);
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderID = createShader(shaderCode, GL30.GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderID = createShader(shaderCode, GL30.GL_FRAGMENT_SHADER);
    }

    public int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderID = GL30.glCreateShader(shaderType);
        if (shaderID == 0)
            throw new Exception("Error creating shader. Type : " + shaderType);
        GL30.glShaderSource(shaderID, shaderCode);
        GL30.glCompileShader(shaderID);

        if(GL30.glGetShaderi(shaderID,GL30.GL_COMPILE_STATUS) == 0)
            throw new Exception("Error compiling shader. Type : " + shaderType + "\n" + GL30.glGetShaderInfoLog(shaderID, 1024));

        GL30.glAttachShader(programId, shaderID);
        return shaderID;
    }

    public void link() throws Exception {
        GL30.glLinkProgram(programId);
        /*if(GL30.glGetProgrami(programId,GL30.GL_COMPILE_STATUS) == 0)
            throw new Exception("Error compiling shader. Info : " + GL30.glGetShaderInfoLog(programId, 1024));*/

        if(vertexShaderID != 0)
            GL30.glDetachShader(programId, vertexShaderID);
        if (fragmentShaderID != 0)
            GL30.glDetachShader(programId, fragmentShaderID);

        GL30.glValidateProgram(programId);

        if(GL30.glGetProgrami(programId, GL30.GL_VALIDATE_STATUS) == 0)
            throw new Exception("Unable validating shader code. Info : " + GL30.glGetProgramInfoLog(programId, 1024));
    }

    public void bind() {
        GL30.glUseProgram(programId);
    }

    public void unbind() {
        GL30.glUseProgram(0);
    }

    public void cleanup() {
       unbind();
       if(programId != 0)
           GL30.glDeleteProgram(programId);
    }
}
