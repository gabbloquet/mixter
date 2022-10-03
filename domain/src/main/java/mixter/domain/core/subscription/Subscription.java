package mixter.domain.core.subscription;

import mixter.doc.Aggregate;
import mixter.doc.Projection;
import mixter.domain.DecisionProjectionBase;
import mixter.domain.Event;
import mixter.domain.EventPublisher;
import mixter.domain.core.message.MessageId;
import mixter.domain.core.subscription.events.FolloweeMessageQuacked;
import mixter.domain.core.subscription.events.UserFollowed;
import mixter.domain.core.subscription.events.UserUnfollowed;
import mixter.domain.identity.UserId;

import java.util.ArrayList;
import java.util.List;

@Aggregate
public class Subscription {

    private DecisionProjection projection;

    public Subscription(List<Event> history) {
        this.projection = new DecisionProjection(history);
    }

    public static void follow(UserId follower, UserId followee, EventPublisher eventPublisher) {
        UserFollowed userFollowed = new UserFollowed(new SubscriptionId(follower, followee));

        eventPublisher.publish(userFollowed);
    }

    public void unfollow(EventPublisher eventPublisher) {
        if(projection.subscriptions.isEmpty())
            return;

        SubscriptionId subscriptionId = projection.subscriptions.get(0);
        UserUnfollowed userUnfollowed = new UserUnfollowed(subscriptionId);
        eventPublisher.publish(userUnfollowed);
    }

    public void notifyFollower(MessageId messageId, EventPublisher eventPublisher) {
        if(projection.subscriptions.isEmpty())
            return;
        SubscriptionId subscriptionId = projection.subscriptions.get(0);
        FolloweeMessageQuacked event = new FolloweeMessageQuacked(subscriptionId, messageId);

        eventPublisher.publish(event);
    }

    @Projection
    private class DecisionProjection extends DecisionProjectionBase {

        private SubscriptionId id;
        private List<SubscriptionId> subscriptions = new ArrayList<>();

        public DecisionProjection(List<Event> history) {
            super.register(UserFollowed.class, this::apply);
            super.register(UserUnfollowed.class, this::apply);
            history.forEach(this::apply);
        }

        private void apply(UserFollowed event) {
            id = event.getSubscriptionId();
            subscriptions.add(event.getSubscriptionId());
        }

        private void apply(UserUnfollowed event) {
            subscriptions.remove(event.getSubscriptionId());
        }
    }
}
