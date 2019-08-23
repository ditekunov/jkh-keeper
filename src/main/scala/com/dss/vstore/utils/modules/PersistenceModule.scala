package com.dss.vstore.utils.modules

trait PersistenceModule {
  //  val clientDal: ClientDal
  //  val clientDataDal: ClientDataDal
  //  val adminDal: AdminDal
  //  val bankDal: BankDal
  //  val counterDal: CounterDal
  //  val receiverDal: ReceiverDal
  def dbConnectionClose: Unit
}

trait PersistenceModuleImpl extends PersistenceModule {
  this: ConfigurationModule with ActorModule =>

  override def dbConnectionClose: Unit = {
    system.log.info("Database connection is now closing")
    //    cassandraContext.close()
    //    cassandraStreamContext.close()
    system.log.info("Database connection closed")
  }

  //  override lazy val cassandraContext =
  //    new CassandraAsyncContext(
  //      Literal,
  //      cluster.get,
  //      config.get.parsed.cassandra.keyspace,
  //      config.get.raw.getInt("cassandra.preparedStatementCacheSize")
  //    )

  //  override lazy val cassandraStreamContext =
  //    new CassandraStreamContext(
  //      Literal,
  //      cluster.get,
  //      config.get.parsed.cassandra.keyspace,
  //      config.get.raw.getInt("cassandra.preparedStatementCacheSize")
  //    )

  //  override lazy val clientDal = new ClientDalImpl(cassandraContext)
  //
  //  override lazy val clientDataDal = new ClientDataDalImpl(cassandraContext)
  //
  //  override lazy val adminDal = new AdminDalImpl(cassandraContext)
  //
  //  override lazy val bankDal = new BankDalImpl(cassandraContext)
  //
  //  override lazy val counterDal = new CounterDalImpl(cassandraContext)
  //
  //  override lazy val receiverDal = new ReceiverDalImpl(cassandraContext)
}
