package mixter.domain.core.subscription.handlers;

import mixter.domain.EventPublisher;
import mixter.domain.core.message.MessageId;
import mixter.domain.core.message.events.MessageQuacked;
import mixter.domain.core.message.events.MessageRequacked;
import mixter.domain.core.subscription.FollowerRepository;
import mixter.domain.core.subscription.SubscriptionId;
import mixter.domain.core.subscription.SubscriptionRepository;
import mixter.domain.core.subscription.events.FolloweeMessageQuacked;
import mixter.domain.identity.UserId;

import java.util.ArrayList;
import java.util.Set;

public class NotifyFollowerOfFolloweeMessage {

    private final FollowerRepository followerRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final EventPublisher eventPublisher;

    public NotifyFollowerOfFolloweeMessage(FollowerRepository followerRepository, SubscriptionRepository subscriptionRepository, EventPublisher eventPublisher) {
        this.followerRepository = followerRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.eventPublisher = eventPublisher;
    }

    public void apply(MessageQuacked messageQuacked) {
        sendFolloweeMessageQuackedEvent(messageQuacked.getAuthorId(), messageQuacked.getMessageId());
    }

    public void apply(MessageRequacked messageRequacked) {
        sendFolloweeMessageQuackedEvent(messageRequacked.getAuthorId(), messageRequacked.getMessageId());
    }

    private void sendFolloweeMessageQuackedEvent(UserId authorId, MessageId messageId) {
        ArrayList<FolloweeMessageQuacked> events = new ArrayList<>();
        Set<UserId> followers = followerRepository.getFollowers(authorId);

        followers.forEach(follower -> {
            events.add(new FolloweeMessageQuacked(new SubscriptionId(follower, authorId), messageId));
        });

        events.forEach(this.eventPublisher::publish);
    }
}
