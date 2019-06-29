package cz.tefek.kekminer.adventure;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public class MessageUserBinding
{
    private static final Map<Long, Long> bindings = new ConcurrentHashMap<>();

    public static void add(User user, Message mes)
    {
        bindings.put(user.getIdLong(), mes.getIdLong());
    }

    public static Optional<Long> get(User user)
    {
        return Optional.ofNullable(bindings.get(user.getIdLong()));
    }

    public static Optional<Long> get(long user)
    {
        return Optional.ofNullable(bindings.get(user));
    }
}
