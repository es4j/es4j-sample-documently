package com.lingona.es4j.sample.documently.domain.commandhandlers;

//using System;
//using CommonDomain.Persistence;
//using Documently.Commands;
//using Documently.Domain.Domain;
//using Magnum;
//using MassTransit;


public class CreateCustomerCommandHandler implements Consumes<CreateNewCustomer>.All {
    private final Func<IRepository> _Repository;

    public CreateCustomerCommandHandler(Func<IRepository> repository) {
			_Repository = repository;
    }

    public void Consume(CreateNewCustomer command) {
			var repo = _Repository();

			var client = Customer.CreateNew(command.AggregateId, new CustomerName(command.CustomerName),
			                                new Address(command.Street, command.StreetNumber,
			                                            command.PostalCode, command.City),
			                                new PhoneNumber(command.PhoneNumber));

			repo.Save(client, CombGuid.Generate(), null);
    }
}