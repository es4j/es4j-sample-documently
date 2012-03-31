using System;
using System.Collections.Generic;

namespace Documently.Commands
{
    [Serializable]
    public class ShareDocument : Command
    {
    	public ShareDocument(Guid arId, int version, IEnumerable<int> userIds)
    	{
    		UserIds = userIds;
    		AggregateId = arId;
    		Version = version;
    	}

    	public IEnumerable<int> UserIds { get; protected set; }
    	public Guid AggregateId { get; set; }
    	public int Version { get; set; }
    }
}