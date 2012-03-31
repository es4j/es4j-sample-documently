﻿using System.Linq;
using Raven.Client;

namespace Documently.ReadModel
{
	public class RavenReadRepository : IReadRepository
	{
		private readonly IDocumentStore _documentStore;

		public RavenReadRepository(IDocumentStore documentStore)
		{
			_documentStore = documentStore;
		}

		public IQueryable<T> GetAll<T>() where T : class
		{
			using (var session = _documentStore.OpenSession())
			{
				return session.Query<T>();
			}
		}

		public T GetById<T>(string id) where T : class
		{
			using (var session = _documentStore.OpenSession())
			{
				return session.Load<T>(id);
			}
		}
	}
}