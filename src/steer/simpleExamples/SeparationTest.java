/*
Copyright (c) 2014, Jesús Martín Berlanga
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this 
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, 
this list of conditions and the following disclaimer in the documentation 
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software without 
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package steer.simpleExamples;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;


import com.jme3.ai.agents.behaviours.npc.SimpleMainBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.SeparationBehaviour;

import steer.control.CustomSteerControl;

/**
 * AI Steer Test - Testing the seek combined with separation behaviour
 * 
 * @author Jesús Martín Berlanga
 * @version 1.0
 */
public class SeparationTest extends SimpleApplication {
    
    private Game game = Game.getInstance(); //creating game
    
    //TEST SETTINGS - START
    private final String BOID_MODEL_NAME = "Models/boid.j3o";
    private final String BOID_MATERIAL_NAME = "Common/MatDefs/Misc/Unshaded.j3md";
    private final int NUMBER_NEIGHBOURS = 15;
    private final ColorRGBA NEIGHBOURS_COLOR = ColorRGBA.Blue;
    private final float NEIGHBOURS_MOVE_SPEED = 1f;
    private final float NEIGHBOURS_ROTATION_SPEED = 30;
    private final float NEIGHBOURS_MASS = 50;
    private final float NEIGHBOURS_MAX_FORCE = 20;
    //TEST SETTINGS - END

    public static void main(String[] args) {
        SeparationTest app = new SeparationTest();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        //defining rootNode for game processing
        game.setRootNode(rootNode);
        
        game.setInputManager(inputManager);
        game.setGameControl(new CustomSteerControl());
        game.getGameControl().loadInputManagerMapping();
        
        this.setupCamera();
        
        Vector3f[] spawnArea = null;
        
                
        Agent[] neighbours = new Agent[this.NUMBER_NEIGHBOURS];

        for(int i = 0; i < this.NUMBER_NEIGHBOURS; i++){
            neighbours[i] = this.createBoid("Neighbour " + i, this.NEIGHBOURS_COLOR);
            game.addAgent(neighbours[i]); //Add the neighbours to the game
            this.setStats(neighbours[i], this.NEIGHBOURS_MOVE_SPEED,
                    this.NEIGHBOURS_ROTATION_SPEED, this.NEIGHBOURS_MASS,
                    this.NEIGHBOURS_MAX_FORCE);
            game.getGameControl().spawn(neighbours[i], spawnArea);
        }
        
        //ADD BEHAVIOURS - START
       
        SimpleMainBehaviour[] neighboursMainBehaviour = new SimpleMainBehaviour[neighbours.length];
        
        for(int i = 0; i < neighbours.length; i++){
            neighboursMainBehaviour[i] = new SimpleMainBehaviour(neighbours[i]);
            List<Agent> obstacles = removeAgentFromList(Arrays.asList(neighbours), neighbours[i]);
            neighboursMainBehaviour[i].addBehaviour(new SeparationBehaviour(neighbours[i], obstacles));
            neighbours[i].setMainBehaviour(neighboursMainBehaviour[i]);
        }
        
        //ADD BEHAVIOURS - END
        
        game.start();
    }
    
    private void setupCamera(){
        getCamera().setLocation(new Vector3f(0,20,0));
        getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_X);
        getFlyByCamera().setMoveSpeed(50);
        
        
         /*// Disable flying camera - DEBUG ONLY
         flyCam.setDragToRotate(true);
         flyCam.setEnabled(false);*/
    }
    
    //Create an agent with a name and a color
    private Agent createBoid(String name, ColorRGBA color){
            Spatial boidSpatial = assetManager.loadModel(this.BOID_MODEL_NAME);
            boidSpatial.setLocalScale(0.1f); //Resize
 
            Material mat = new Material(assetManager, this.BOID_MATERIAL_NAME);                         
            mat.setColor("Color", color);
            boidSpatial.setMaterial(mat);
            
            return new Agent(name, boidSpatial);
    }
    
    //Setup the stats for an agent
    private void setStats(Agent myAgent, float moveSpeed, float rotationSpeed,
            float mass, float maxForce){
        
            myAgent.setMoveSpeed(moveSpeed);
            myAgent.setRotationSpeed(rotationSpeed);
            myAgent.setMass(mass);
            myAgent.setMaxForce(maxForce);
    }
    
    //Return the given Agent array without the element in the index
    private List<Agent> removeAgentFromList(List<Agent> agents, Agent removeAgent){

        List<Agent> newAgents = new ArrayList<Agent>(agents);

        Iterator<Agent> i = newAgents.iterator();
        boolean removed = false;
        
        while (!removed &&  i.hasNext()) {
            Agent a = i.next();
            
            if(a.equals(removeAgent)){
                i.remove();
                removed = true;
            }
        }
        
        return newAgents;
    }
    
    @Override
    public void simpleUpdate(float tpf) { 
        game.update(tpf);
    }
}
