package simulation.evolution.events;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.events.GameObjectEvent;

/**
 *
 * @author Tihomir Radosavljević
 */
public class BeingAttackedEvent extends GameObjectEvent{

    private Agent attackingAgent;
    
    public BeingAttackedEvent(Object source, Agent attackingAgent) {
        super(source);
        this.attackingAgent = attackingAgent;
    }

    public Agent getAttackingAgent() {
        return attackingAgent;
    }

}
