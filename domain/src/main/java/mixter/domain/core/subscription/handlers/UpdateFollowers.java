package mixter.domain.core.subscription.handlers;

import mixter.doc.Handler;
import mixter.domain.core.subscription.FollowerRepository;
import mixter.domain.core.subscription.events.UserFollowed;
import mixter.domain.core.subscription.events.UserUnfollowed;

@Handler
public class UpdateFollowers {

    private final FollowerRepository repository;

    public UpdateFollowers(FollowerRepository repository) {
        this.repository = repository;
    }

    public void apply(UserFollowed userFollowed) {
        repository.saveFollower(userFollowed.getSubscriptionId().getFollowee(), userFollowed.getSubscriptionId().getFollower());
    }

    public void apply(UserUnfollowed userUnfollowed) {
        repository.removeFollower(userUnfollowed.getSubscriptionId().getFollowee(), userUnfollowed.getSubscriptionId().getFollower());
    }
}
