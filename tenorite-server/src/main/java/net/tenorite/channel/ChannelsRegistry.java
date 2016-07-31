package net.tenorite.channel;

import net.tenorite.core.Tempo;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * @author Johan Siebens
 */
public interface ChannelsRegistry {

    CompletionStage<List<Channel>> listChannels(Tempo tempo);

}
