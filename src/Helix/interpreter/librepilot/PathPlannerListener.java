package Helix.interpreter.librepilot;

public interface PathPlannerListener {

    public void onProgressUpdate(float progress);

    public void onFinishPath();

    public void onError();
}
