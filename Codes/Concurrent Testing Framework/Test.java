import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {}


/*
 Fine-Grained Synchronization:
    This involves locking smaller sections of code or individual objects.

 Optimistic Synchronization:
    This approach assumes that conflicts are rare and uses techniques like compare-and-swap operations.

 Course-Grained Synchronization:
    This type of synchronization involves locking larger sections of code or entire methods.
 */
