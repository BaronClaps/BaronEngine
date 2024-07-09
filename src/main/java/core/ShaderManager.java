package Baron_Engine.core;

import org.lwjgl.opengl.GL30;

public class ShaderManager {
    private final int programId;
    private int vertexShaderID, fragmentShaderID;

    public ShaderManager() throws Exception {
        programId = GL30.glCreateProgram();
        if (programId == 0)
            throw new Exception("Cannot create shader");
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
        if(GL30.glGetProgrami(programId,GL30.GL_COMPILE_STATUS) == 0)
            throw new Exception("Error compiling shader. Info : " + GL30.glGetShaderInfoLog(programId, 1024));

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
