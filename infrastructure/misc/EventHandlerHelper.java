using System;
using System.Collections.Generic;
using System.Linq;
using Documently.Messages;
using Documently.ReadModel;

namespace Documently.Infrastructure.Misc
{
	public static class EventHandlerHelper
	{
		public static IDictionary<Type, IList<Type>> GetEventHandlers()
		{
			var handlers = new Dictionary<Type, IList<Type>>();
			typeof (CustomerListView)
				.Assembly
				.GetExportedTypes()
				.Where(x => x.GetInterfaces().Any(y => y.IsGenericType && y.GetGenericTypeDefinition() == typeof (HandlesEvent<>)))
				.ToList()
				.ForEach(x => AddItem(handlers, x));

			return handlers;
		}

		public static IEnumerable<Type> GetEvents()
		{
			return typeof (DomainEvent)
				.Assembly
				.GetExportedTypes()
				.Where(x => x.BaseType == typeof (DomainEvent))
				.ToList();
		}

		private static void AddItem(IDictionary<Type, IList<Type>> dictionary, Type type)
		{
			var theEvent = type.GetInterfaces()
				.Where(x => x.IsGenericType && x.GetGenericTypeDefinition() == typeof (HandlesEvent<>))
				.First()
				.GetGenericArguments()
				.First();

			if (!dictionary.ContainsKey(theEvent))
				dictionary.Add(theEvent, new List<Type>());

			dictionary[theEvent].Add(type);
		}
	}
}