package Helix.interpreter.controller.librepilot;

public interface PathPlanListener {

    public void onProgressUpdate(float progress);

    public void onFinishPath();

    public void onError();
}
