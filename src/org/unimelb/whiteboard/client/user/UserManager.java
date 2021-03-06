package org.unimelb.whiteboard.client.user;

import org.unimelb.whiteboard.client.chatroom.ChatPanel;
import org.unimelb.whiteboard.client.remote.IRemoteApp;
import org.unimelb.whiteboard.client.remote.IRemotePaint;
import org.unimelb.whiteboard.client.remote.IRemoteUM;
import org.unimelb.whiteboard.client.whiteboard.ClientListScrollPanel;
import org.unimelb.whiteboard.client.whiteboard.PaintManager;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;


public class UserManager {
    // Manager info
    private final User manager;
    private final Boolean isManager;
    private PaintManager managerPaintManager;
    private int managerChatPort;
    private IRemotePaint managerRemotePaint;
    private IRemoteUM managerRemoteUM;
    private IRemoteApp managerRemoteApp;
    // Applicants info
    private final Map<String, User> applicants;
    private final Map<String, IRemoteApp> applicantRemoteApps;
    // Members info
    private Map<String, User> members;
    private final Map<String, IRemotePaint> memberRemotePaints;
    private final Map<String, IRemoteApp> memberRemoteApps;
    private final Map<String, IRemoteUM> memberRemoteUMs;
    // Use to refresh ui
    ClientListScrollPanel clsp;
    ChatPanel chatPanel;

    public UserManager(Boolean isManager, String userId, String ip, int registerPort, int chatPort) {
        this.isManager = isManager;

        this.manager = new User(userId, User.MANAGER, ip, registerPort, chatPort);
        if (!isManager) {
            try {
                Registry registry = LocateRegistry.getRegistry(ip, registerPort);
                managerRemotePaint = (IRemotePaint) registry.lookup("paint");
                managerRemoteUM = (IRemoteUM) registry.lookup("um");
                managerRemoteApp = (IRemoteApp) registry.lookup("app");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        members = new HashMap<>();
        memberRemotePaints = new HashMap<>();
        memberRemoteUMs = new HashMap<>();
        memberRemoteApps = new HashMap<>();

        applicants = new HashMap<>();
        applicantRemoteApps = new HashMap<>();
    }

    /**
     * Get the manager chat port.
     */
    public int getManagerChatPort() {
        return managerChatPort;
    }

    /**
     * Set the manager's chat port.
     */
    public void setManagerChatPort(int chatPort) {
        this.managerChatPort = chatPort;
    }

    public void setChatPanel(ChatPanel chatPanel) {
        this.chatPanel = chatPanel;
    }

    /**
     * This method is for manager, set the manager paint manager.
     */
    public void setManagerPaintManager(PaintManager paintManager) {
        if (isManager)
            this.managerPaintManager = paintManager;
    }

    /**
     * Remove all the member and applicant.
     */
    public void clear() {
        for (IRemoteApp remoteApp : memberRemoteApps.values()) {
            try {
                remoteApp.askOut();
            } catch (Exception e) {
                System.out.println("Discover a member has network problem when asking him out.");
            }
        }

        for (IRemoteApp remoteApp : applicantRemoteApps.values()) {
            try {
                remoteApp.askOut();
            } catch (Exception e) {
                System.out.println("Discover a applicant has network problem when asking him out.");
            }
        }

        memberRemoteApps.clear();
        memberRemotePaints.clear();
        memberRemoteUMs.clear();
        members.clear();

        applicantRemoteApps.clear();
        applicants.clear();
    }

    /**
     * Check if the user is manager.
     */
    public Boolean isManager() {
        return isManager;
    }

    /**
     * Get remote UM.
     */
    public IRemoteUM getManagerRemoteUM() {
        return managerRemoteUM;
    }

    /**
     * Add a applicant to member list, open the door for a applicant.
     */
    public void addMember(String userId) {
        System.out.println("Allow " + userId + " enter.");
        // add member
        User member = applicants.get(userId);
        members.put(userId, member);
        memberRemoteApps.put(userId, applicantRemoteApps.get(userId));
        try {
            Registry clientRegistry = LocateRegistry.getRegistry(member.getIp(), member.getRegisterPort());
            IRemoteApp memberRemoteApp = (IRemoteApp) clientRegistry.lookup("app");
            memberRemoteApp.askIn(manager.getIp(), managerChatPort);

            IRemotePaint remotePaint = (IRemotePaint) clientRegistry.lookup("paint");
            memberRemotePaints.put(userId, remotePaint);
            if (managerPaintManager != null) {
                remotePaint.setHistory(managerPaintManager.getPaintHistory());
            }

            IRemoteUM remoteUM = (IRemoteUM) clientRegistry.lookup("um");
            memberRemoteUMs.put(userId, remoteUM);

            // delete applicant
            applicants.remove(userId);
            applicantRemoteApps.remove(userId);
        } catch (Exception e) {
            removeMember(userId);
            applicants.remove(userId);
            applicantRemoteApps.remove(userId);
            System.err.println("Can't connect to member " + userId + ", Remove.");
        }

        // set remote user manager
        for (String key : memberRemoteUMs.keySet()) {
            try {
                memberRemoteUMs.get(key).setMembers(members);
            } catch (RemoteException e) {
                System.err.println("Can't connect to member " + key + ", Remove.");
                removeMember(key);
            }
        }

        // refresh ui.
        if (clsp != null) {
            clsp.updateUserList();
        }
    }

    /**
     * Remove a member from member list.
     */
    public void removeMember(String userId) {
        members.remove(userId);
        memberRemoteUMs.remove(userId);
        memberRemotePaints.remove(userId);
        memberRemoteApps.remove(userId);

        // set remote user manager
        for (String key : memberRemoteUMs.keySet()) {
            try {
                memberRemoteUMs.get(key).setMembers(members);
            } catch (RemoteException e) {
                System.err.println("Can't connect to member " + key + ", Remove.");
                removeMember(key);
            }
        }
        // refresh ui.
        if (clsp != null) {
            clsp.updateUserList();
        }
    }

    /**
     * Kick a member out of the room.
     */
    public void kickMember(String userId) {
        System.out.println("Kick " + userId + " out.");
        try {
            IRemoteApp remoteApp = memberRemoteApps.get(userId);
            remoteApp.askOut();
        } catch (Exception e) {
            System.err.println("Can't connect to member " + userId + ", Remove.");
        }
        removeMember(userId);
    }

    /**
     * Get all the members' remotePaints
     */
    public Map<String, IRemotePaint> getMemberRemotePaints() {
        return memberRemotePaints;
    }

    /**
     * Add a applicant to the applicant list, when the applicant knock the door.
     */
    public void addApplicant(String userId, String ip, int registerPort) {
        applicants.put(userId, new User(userId, User.APPLICANT, ip, registerPort, -1));
        try {
            Registry clientRegistry = LocateRegistry.getRegistry(ip, registerPort);
            IRemoteApp remoteApplicantApp = (IRemoteApp) clientRegistry.lookup("app");
            applicantRemoteApps.put(userId, remoteApplicantApp);
        } catch (Exception e) {
            System.err.println("Can not get the client registry.");
        }
        // refresh ui.
        if (clsp != null) {
            clsp.updateUserList();
        }
    }

    /**
     * Remove the applicant from the applicant list.
     */
    public void removeApplicant(String userId) {
        applicants.remove(userId);
        applicantRemoteApps.remove(userId);
        // refresh ui.
        if (clsp != null) {
            clsp.updateUserList();
        }
    }

    public void kickApplicant(String userId) {
        try {
            IRemoteApp remoteApp = applicantRemoteApps.get(userId);
            remoteApp.askOut();
        } catch (Exception e) {
            System.err.println("Can not get the client registry.");
        }
        removeApplicant(userId);
    }

    /**
     * Get member list.
     */
    public Map<String, User> getMembers() {
        return members;
    }

    /**
     * Update all the members.
     */
    public void setMembers(Map<String, User> members) {
        this.members = members;
        // refresh ui.
        if (clsp != null) {
            clsp.updateUserList();
        }
    }

    /**
     * Get applicant list.
     */
    public Map<String, User> getApplicants() {
        return applicants;
    }

    /**
     * Get host.
     */
    public User getManager() {
        return manager;
    }

    /**
     * Get the remotePaint of host
     */
    public IRemotePaint getManagerRemotePaint() {
        return managerRemotePaint;
    }

    /**
     * Set the clientListScrollPanel.
     */
    public void setCLSP(ClientListScrollPanel clsp) {
        this.clsp = clsp;
    }

    /**
     * Get the identity of a given userId.
     */
    public int getIdentity(String userId) {
        if (manager.getUserId().equals(userId)) {
            return User.MANAGER;
        } else if (members.containsKey(userId)) {
            return User.MEMBER;
        } else {
            return User.APPLICANT;
        }
    }

    public IRemoteApp getManagerRemoteApp() {
        return managerRemoteApp;
    }

    public void setManagerRemoteApp(IRemoteApp managerRemoteApp) {
        this.managerRemoteApp = managerRemoteApp;
    }
}
