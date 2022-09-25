package seng202.team3.data.entity;

/**
 * Enum to control user permissions and feature restriction
 */
public enum PermissionLevel {

    /**
     * Basic User
     */
    USER,

    /**
     * Charger Owner
     */
    CHARGEROWNER,

    /**
     * Administrator
     */
    ADMIN;

    /**
     * Get the permission level for the user from int value
     * 
     * @param i value to get permission
     * @return mapped permission level
     */
    public static PermissionLevel fromValue(int i) {
        switch (i) {
            case 0:
                return USER;
            case 1:
                return CHARGEROWNER;
            case 2:
                return ADMIN;
            default:
                throw new IllegalArgumentException("Invalid PermissionLevel value");
        }
    }
}
