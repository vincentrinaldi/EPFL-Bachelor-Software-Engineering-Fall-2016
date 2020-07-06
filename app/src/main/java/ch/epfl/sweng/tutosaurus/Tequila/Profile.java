package ch.epfl.sweng.tutosaurus.Tequila;

/**
 * Tequila user profile information.
 *
 * @author Solal Pirelli
 */
public final class Profile {
    /**
     * This is the user ID, it is guaranteed to be unique.
     */
    private final String sciper;
    /**
     * This is probably unique, but you shouldn't depend on it.
     */
    private final String gaspar;
    /**
     * Don't spam your users! Use this carefully.
     */
    private final String email;
    /**
     * Do not assume anything about what exactly this contains.
     * Some people have one name, some have multiple, some have honorary prefixes, ...
     */
    private final String firstNames;
    /**
     * Same remark as `firstNames`.
     */
    private final String lastNames;

    /**
     * Create User Profile. Profiles are always fetched by Tequila and thus assumed to be automatically correct.
     *
     * @param sciper     the sciper associated with this profile
     * @param gaspar     the gaspar associated with this profile
     * @param email      the email associated with this profile
     * @param firstNames the first names of this profile's user
     * @param lastNames  the last names of this profile's user
     */
    public Profile(String sciper, String gaspar, String email, String firstNames, String lastNames) {
        if (sciper == null || gaspar == null || email == null ||
                firstNames == null || lastNames == null) {
            throw new IllegalArgumentException("null tequila profile");
        } else {
            this.sciper = sciper;
            this.gaspar = gaspar;
            this.email = email;
            this.firstNames = firstNames;
            this.lastNames = lastNames;
        }
    }

    @Override
    public String toString() {
        return firstNames + " " + lastNames
                + "\nsciper: " + sciper
                + "\ngaspar: " + gaspar
                + "\nemail: " + email;
    }

    public String getFirstNames() {
        return firstNames;
    }

    public String getLastNames() {
        return lastNames;
    }

    public String getSciper() {
        return sciper;
    }

    public String getEmail() {
        return email;
    }

    public String getGaspar() {
        return gaspar;
    }
}