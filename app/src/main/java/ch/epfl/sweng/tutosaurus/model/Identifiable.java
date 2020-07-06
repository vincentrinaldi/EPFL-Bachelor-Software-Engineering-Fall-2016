package ch.epfl.sweng.tutosaurus.model;

/**
 * An interface for classes whose instances that can be identified by an unique id and a human-readable full name.
 */
public interface Identifiable {

    String getUid();
    String getFullName();
}