package Baron_Engine.core;

public interface ILogic {
    void init() throws Exception;
    void input();
    void update();
    void render();
    void cleanup();

}

