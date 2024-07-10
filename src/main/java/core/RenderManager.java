package core;

import core.entity.Model;
import core.utils.Utils;
import test.Launcher;
import org.lwjgl.opengl.GL30;

public class RenderManager {

    private final WindowManager window;
    private ShaderManager shader;

    public RenderManager() {
        window = Launcher.getWindow();
    }

    public void init() throws Exception {
        shader = new ShaderManager();
        shader.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
        shader.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
        shader.link();
        shader.createUniform("textureSampler");
    }

    public void render(Model model) {
        clear();
        shader.bind();
        shader.setUniforms("textureSampler", 0);
        GL30.glBindVertexArray(model.getId());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, model.getTexture().getId());
        GL30.glDrawElements(GL30.GL_TRIANGLES, model.getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
        shader.unbind();
    }

    public void clear(){
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        shader.cleanup();
    }
}
