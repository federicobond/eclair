package fr.acinq.eclair.router

import java.nio.ByteOrder

import fr.acinq.bitcoin.{Block, Protocol}
import fr.acinq.eclair.router.Announcements.zip
import fr.acinq.eclair.wire.ReplyChannelRange
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scala.annotation.tailrec

@RunWith(classOf[JUnitRunner])
class QueryChannelsSpec extends FunSuite {
  val shortChannelIds = QueryChannelsSpec.readShortChannelIds()

  test("create `reply_channel_range` messages") {
    val reply = ReplyChannelRange(Block.RegtestGenesisBlock.blockId, 0, 2000000, zip(shortChannelIds))
    val unzipped = Announcements.unzip(reply.data)
    assert(unzipped == shortChannelIds.toSet)
  }
}

object QueryChannelsSpec {
  def readShortChannelIds() = {
    val stream = classOf[QueryChannelsSpec].getResourceAsStream("/short_channels-mainnet.422")

    @tailrec
    def loop(acc: Vector[Long] = Vector()): Vector[Long] = if (stream.available() == 0) acc else loop(acc :+ Protocol.uint64(stream, ByteOrder.BIG_ENDIAN))

    try {
      loop()
    }
    finally {
      stream.close()
    }
  }
}
