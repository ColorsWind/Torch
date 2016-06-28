package org.spigotmc;

import net.minecraft.server.MinecraftServer;
// TacoSpigot start
import java.util.List;
import java.util.Set;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.ForwardingSet;
// TacoSpigot end

public class AsyncCatcher
{

    public static boolean enabled = true; // Torch - permit magic

    public static void catchOp(String reason)
    {
        if ( enabled && Thread.currentThread() != MinecraftServer.getServer().primaryThread )
        {
			MinecraftServer.getServer().postToMainThread(Thread.currentThread());
            MinecraftServer.LOGGER.warn(reason + " called async on " + Thread.currentThread().getName()); // TacoSpigot - log
            throw new IllegalStateException( "Asynchronous " + reason + " on thread " + Thread.currentThread().getName() + "!" ); // TacoSpigot - give thread
        }
    }

    // TacoSpigot start - safety wrappers
    public static <E> List<E> catchAsyncUsage(List<E> list, String msg) {
        return new ForwardingList<E>() {
            @Override
            protected List<E> delegate() {
                AsyncCatcher.catchOp(msg);
                return list;
            }
        };
    }

    public static <E> Set<E> catchAsyncUsage(Set<E> set, String msg) {
        return new ForwardingSet<E>() {
            @Override
            protected Set<E> delegate() {
                AsyncCatcher.catchOp(msg);
                return set;
            }
        };
    }
    // TacoSpigot end
	
	//hose
    public static boolean catchOp_flag()
    {
        if ( enabled && Thread.currentThread() != MinecraftServer.getServer().primaryThread )
        {
            //return true;
            MinecraftServer.getServer().postToMainThread(Thread.currentThread());
            System.out.println("post to main thread");
        }
        return false;
    }
}