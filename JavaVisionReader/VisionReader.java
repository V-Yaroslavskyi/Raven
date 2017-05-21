import coppelia.IntW;
import coppelia.IntWA;
import coppelia.CharWA;
import coppelia.remoteApi;

public class VisionReader
{
    public static void main(String[] args)
    {
        System.out.println("Program started");
        remoteApi vrep = new remoteApi();
        vrep.simxFinish(-1); // just in case, close all opened connections
        int clientID = vrep.simxStart("127.0.0.1",19997,true,true,5000,5);
        if (clientID!=-1)
        {
            System.out.println("Connected to remote API server");

            int resolution_size = 256;

            CharWA image = new CharWA(resolution_size * resolution_size * 3);
            IntWA resolution = new IntWA(resolution_size * resolution_size * 3);
            IntW cameraHandle = new IntW(0);

            System.out.println("Obtaining camera handle");
            vrep.simxGetObjectHandle(clientID, "CopterCamera", cameraHandle, vrep.simx_opmode_oneshot_wait);

            System.out.println("Obtaining first image");
            vrep.simxGetVisionSensorImage(clientID, 
                                          cameraHandle.getValue(), 
                                          resolution, 
                                          image, 
                                          0, 
                                          vrep.simx_opmode_streaming);

            System.out.println("About to enter some strange loop");
            while (vrep.simxGetConnectionId(clientID) != -1)
            {
                int errorCode = vrep.simxGetVisionSensorImage(clientID, cameraHandle.getValue(), resolution, image, 0, vrep.simx_opmode_buffer);

                if (errorCode == vrep.simx_error_noerror)
                {
                    // Do stuff?
                }

                else
                {
                    System.out.println("There is some error");
                    break;
                }
            }

            // Before closing the connection to V-REP, make sure that the 
            // last command sent out had time to arrive. 
            // You can guarantee this with (for example):
            IntW pingTime = new IntW(0);
            vrep.simxGetPingTime(clientID,pingTime);

            // Now close the connection to V-REP:   
            vrep.simxFinish(clientID);
        }
        else
            System.out.println("Failed connecting to remote API server");

        System.out.println("Program ended");
    }
}
