package io.github.yamaxila.cifrazia.cats.models;

public class AuthRequest {

    private final boolean guest_pc;
    private final String login;
    private final String password;

    public AuthRequest(boolean guest_pc, String login, String password) {
        this.guest_pc = guest_pc;
        this.login = login;
        this.password = password;
    }

    public boolean isGuest_pc() {
        return  this.guest_pc;
    }

    public String getLogin() {
        return  this.login;
    }

    public String getPassword() {
        return  this.password;
    }
}
