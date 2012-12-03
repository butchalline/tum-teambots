// **********************************************************************
//
// Copyright (c) 2003-2011 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************
//
// Ice version 3.4.2
//
// <auto-generated>
//
// Generated from file `DataInterface.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

#include <DataInterface.h>
#include <Ice/LocalException.h>
#include <Ice/ObjectFactory.h>
#include <Ice/BasicStream.h>
#include <IceUtil/Iterator.h>

#ifndef ICE_IGNORE_VERSION
#   if ICE_INT_VERSION / 100 != 304
#       error Ice version mismatch!
#   endif
#   if ICE_INT_VERSION % 100 > 50
#       error Beta header file detected
#   endif
#   if ICE_INT_VERSION % 100 < 2
#       error Ice patch level mismatch!
#   endif
#endif

static const ::std::string __Communication__DataInterface__sendByteData_name = "sendByteData";

static const ::std::string __Communication__DataInterface__sendFloatData_name = "sendFloatData";

::Ice::Object* IceInternal::upCast(::Communication::ByteData* p) { return p; }
::IceProxy::Ice::Object* IceInternal::upCast(::IceProxy::Communication::ByteData* p) { return p; }

::Ice::Object* IceInternal::upCast(::Communication::FloatData* p) { return p; }
::IceProxy::Ice::Object* IceInternal::upCast(::IceProxy::Communication::FloatData* p) { return p; }

::Ice::Object* IceInternal::upCast(::Communication::DataInterface* p) { return p; }
::IceProxy::Ice::Object* IceInternal::upCast(::IceProxy::Communication::DataInterface* p) { return p; }

void
Communication::__read(::IceInternal::BasicStream* __is, ::Communication::ByteDataPrx& v)
{
    ::Ice::ObjectPrx proxy;
    __is->read(proxy);
    if(!proxy)
    {
        v = 0;
    }
    else
    {
        v = new ::IceProxy::Communication::ByteData;
        v->__copyFrom(proxy);
    }
}

void
Communication::__read(::IceInternal::BasicStream* __is, ::Communication::FloatDataPrx& v)
{
    ::Ice::ObjectPrx proxy;
    __is->read(proxy);
    if(!proxy)
    {
        v = 0;
    }
    else
    {
        v = new ::IceProxy::Communication::FloatData;
        v->__copyFrom(proxy);
    }
}

void
Communication::__read(::IceInternal::BasicStream* __is, ::Communication::DataInterfacePrx& v)
{
    ::Ice::ObjectPrx proxy;
    __is->read(proxy);
    if(!proxy)
    {
        v = 0;
    }
    else
    {
        v = new ::IceProxy::Communication::DataInterface;
        v->__copyFrom(proxy);
    }
}

void
Communication::__write(::IceInternal::BasicStream* __os, ::Communication::DataTypeIce v)
{
    __os->write(static_cast< ::Ice::Byte>(v), 7);
}

void
Communication::__read(::IceInternal::BasicStream* __is, ::Communication::DataTypeIce& v)
{
    ::Ice::Byte val;
    __is->read(val, 7);
    v = static_cast< ::Communication::DataTypeIce>(val);
}

const ::std::string&
IceProxy::Communication::ByteData::ice_staticId()
{
    return ::Communication::ByteData::ice_staticId();
}

::IceInternal::Handle< ::IceDelegateM::Ice::Object>
IceProxy::Communication::ByteData::__createDelegateM()
{
    return ::IceInternal::Handle< ::IceDelegateM::Ice::Object>(new ::IceDelegateM::Communication::ByteData);
}

::IceInternal::Handle< ::IceDelegateD::Ice::Object>
IceProxy::Communication::ByteData::__createDelegateD()
{
    return ::IceInternal::Handle< ::IceDelegateD::Ice::Object>(new ::IceDelegateD::Communication::ByteData);
}

::IceProxy::Ice::Object*
IceProxy::Communication::ByteData::__newInstance() const
{
    return new ByteData;
}

const ::std::string&
IceProxy::Communication::FloatData::ice_staticId()
{
    return ::Communication::FloatData::ice_staticId();
}

::IceInternal::Handle< ::IceDelegateM::Ice::Object>
IceProxy::Communication::FloatData::__createDelegateM()
{
    return ::IceInternal::Handle< ::IceDelegateM::Ice::Object>(new ::IceDelegateM::Communication::FloatData);
}

::IceInternal::Handle< ::IceDelegateD::Ice::Object>
IceProxy::Communication::FloatData::__createDelegateD()
{
    return ::IceInternal::Handle< ::IceDelegateD::Ice::Object>(new ::IceDelegateD::Communication::FloatData);
}

::IceProxy::Ice::Object*
IceProxy::Communication::FloatData::__newInstance() const
{
    return new FloatData;
}

void
IceProxy::Communication::DataInterface::sendByteData(const ::Communication::ByteDataPtr& data, const ::Ice::Context* __ctx)
{
    int __cnt = 0;
    while(true)
    {
        ::IceInternal::Handle< ::IceDelegate::Ice::Object> __delBase;
        try
        {
            __delBase = __getDelegate(false);
            ::IceDelegate::Communication::DataInterface* __del = dynamic_cast< ::IceDelegate::Communication::DataInterface*>(__delBase.get());
            __del->sendByteData(data, __ctx);
            return;
        }
        catch(const ::IceInternal::LocalExceptionWrapper& __ex)
        {
            __handleExceptionWrapperRelaxed(__delBase, __ex, true, __cnt);
        }
        catch(const ::Ice::LocalException& __ex)
        {
            __handleException(__delBase, __ex, true, __cnt);
        }
    }
}

::Ice::AsyncResultPtr
IceProxy::Communication::DataInterface::begin_sendByteData(const ::Communication::ByteDataPtr& data, const ::Ice::Context* __ctx, const ::IceInternal::CallbackBasePtr& __del, const ::Ice::LocalObjectPtr& __cookie)
{
    ::IceInternal::OutgoingAsyncPtr __result = new ::IceInternal::OutgoingAsync(this, __Communication__DataInterface__sendByteData_name, __del, __cookie);
    try
    {
        __result->__prepare(__Communication__DataInterface__sendByteData_name, ::Ice::Idempotent, __ctx);
        ::IceInternal::BasicStream* __os = __result->__getOs();
        __os->write(::Ice::ObjectPtr(::IceInternal::upCast(data.get())));
        __os->writePendingObjects();
        __os->endWriteEncaps();
        __result->__send(true);
    }
    catch(const ::Ice::LocalException& __ex)
    {
        __result->__exceptionAsync(__ex);
    }
    return __result;
}

void
IceProxy::Communication::DataInterface::end_sendByteData(const ::Ice::AsyncResultPtr& __result)
{
    __end(__result, __Communication__DataInterface__sendByteData_name);
}

void
IceProxy::Communication::DataInterface::sendFloatData(const ::Communication::FloatDataPtr& data, const ::Ice::Context* __ctx)
{
    int __cnt = 0;
    while(true)
    {
        ::IceInternal::Handle< ::IceDelegate::Ice::Object> __delBase;
        try
        {
            __delBase = __getDelegate(false);
            ::IceDelegate::Communication::DataInterface* __del = dynamic_cast< ::IceDelegate::Communication::DataInterface*>(__delBase.get());
            __del->sendFloatData(data, __ctx);
            return;
        }
        catch(const ::IceInternal::LocalExceptionWrapper& __ex)
        {
            __handleExceptionWrapperRelaxed(__delBase, __ex, true, __cnt);
        }
        catch(const ::Ice::LocalException& __ex)
        {
            __handleException(__delBase, __ex, true, __cnt);
        }
    }
}

::Ice::AsyncResultPtr
IceProxy::Communication::DataInterface::begin_sendFloatData(const ::Communication::FloatDataPtr& data, const ::Ice::Context* __ctx, const ::IceInternal::CallbackBasePtr& __del, const ::Ice::LocalObjectPtr& __cookie)
{
    ::IceInternal::OutgoingAsyncPtr __result = new ::IceInternal::OutgoingAsync(this, __Communication__DataInterface__sendFloatData_name, __del, __cookie);
    try
    {
        __result->__prepare(__Communication__DataInterface__sendFloatData_name, ::Ice::Idempotent, __ctx);
        ::IceInternal::BasicStream* __os = __result->__getOs();
        __os->write(::Ice::ObjectPtr(::IceInternal::upCast(data.get())));
        __os->writePendingObjects();
        __os->endWriteEncaps();
        __result->__send(true);
    }
    catch(const ::Ice::LocalException& __ex)
    {
        __result->__exceptionAsync(__ex);
    }
    return __result;
}

void
IceProxy::Communication::DataInterface::end_sendFloatData(const ::Ice::AsyncResultPtr& __result)
{
    __end(__result, __Communication__DataInterface__sendFloatData_name);
}

const ::std::string&
IceProxy::Communication::DataInterface::ice_staticId()
{
    return ::Communication::DataInterface::ice_staticId();
}

::IceInternal::Handle< ::IceDelegateM::Ice::Object>
IceProxy::Communication::DataInterface::__createDelegateM()
{
    return ::IceInternal::Handle< ::IceDelegateM::Ice::Object>(new ::IceDelegateM::Communication::DataInterface);
}

::IceInternal::Handle< ::IceDelegateD::Ice::Object>
IceProxy::Communication::DataInterface::__createDelegateD()
{
    return ::IceInternal::Handle< ::IceDelegateD::Ice::Object>(new ::IceDelegateD::Communication::DataInterface);
}

::IceProxy::Ice::Object*
IceProxy::Communication::DataInterface::__newInstance() const
{
    return new DataInterface;
}

void
IceDelegateM::Communication::DataInterface::sendByteData(const ::Communication::ByteDataPtr& data, const ::Ice::Context* __context)
{
    ::IceInternal::Outgoing __og(__handler.get(), __Communication__DataInterface__sendByteData_name, ::Ice::Idempotent, __context);
    try
    {
        ::IceInternal::BasicStream* __os = __og.os();
        __os->write(::Ice::ObjectPtr(::IceInternal::upCast(data.get())));
        __os->writePendingObjects();
    }
    catch(const ::Ice::LocalException& __ex)
    {
        __og.abort(__ex);
    }
    bool __ok = __og.invoke();
    if(!__og.is()->b.empty())
    {
        try
        {
            if(!__ok)
            {
                try
                {
                    __og.throwUserException();
                }
                catch(const ::Ice::UserException& __ex)
                {
                    ::Ice::UnknownUserException __uue(__FILE__, __LINE__, __ex.ice_name());
                    throw __uue;
                }
            }
            __og.is()->skipEmptyEncaps();
        }
        catch(const ::Ice::LocalException& __ex)
        {
            throw ::IceInternal::LocalExceptionWrapper(__ex, false);
        }
    }
}

void
IceDelegateM::Communication::DataInterface::sendFloatData(const ::Communication::FloatDataPtr& data, const ::Ice::Context* __context)
{
    ::IceInternal::Outgoing __og(__handler.get(), __Communication__DataInterface__sendFloatData_name, ::Ice::Idempotent, __context);
    try
    {
        ::IceInternal::BasicStream* __os = __og.os();
        __os->write(::Ice::ObjectPtr(::IceInternal::upCast(data.get())));
        __os->writePendingObjects();
    }
    catch(const ::Ice::LocalException& __ex)
    {
        __og.abort(__ex);
    }
    bool __ok = __og.invoke();
    if(!__og.is()->b.empty())
    {
        try
        {
            if(!__ok)
            {
                try
                {
                    __og.throwUserException();
                }
                catch(const ::Ice::UserException& __ex)
                {
                    ::Ice::UnknownUserException __uue(__FILE__, __LINE__, __ex.ice_name());
                    throw __uue;
                }
            }
            __og.is()->skipEmptyEncaps();
        }
        catch(const ::Ice::LocalException& __ex)
        {
            throw ::IceInternal::LocalExceptionWrapper(__ex, false);
        }
    }
}

void
IceDelegateD::Communication::DataInterface::sendByteData(const ::Communication::ByteDataPtr& data, const ::Ice::Context* __context)
{
    class _DirectI : public ::IceInternal::Direct
    {
    public:

        _DirectI(const ::Communication::ByteDataPtr& data, const ::Ice::Current& __current) : 
            ::IceInternal::Direct(__current),
            _m_data(data)
        {
        }
        
        virtual ::Ice::DispatchStatus
        run(::Ice::Object* object)
        {
            ::Communication::DataInterface* servant = dynamic_cast< ::Communication::DataInterface*>(object);
            if(!servant)
            {
                throw ::Ice::OperationNotExistException(__FILE__, __LINE__, _current.id, _current.facet, _current.operation);
            }
            servant->sendByteData(_m_data, _current);
            return ::Ice::DispatchOK;
        }
        
    private:
        
        const ::Communication::ByteDataPtr& _m_data;
    };
    
    ::Ice::Current __current;
    __initCurrent(__current, __Communication__DataInterface__sendByteData_name, ::Ice::Idempotent, __context);
    try
    {
        _DirectI __direct(data, __current);
        try
        {
            __direct.servant()->__collocDispatch(__direct);
        }
        catch(...)
        {
            __direct.destroy();
            throw;
        }
        __direct.destroy();
    }
    catch(const ::Ice::SystemException&)
    {
        throw;
    }
    catch(const ::IceInternal::LocalExceptionWrapper&)
    {
        throw;
    }
    catch(const ::std::exception& __ex)
    {
        ::IceInternal::LocalExceptionWrapper::throwWrapper(__ex);
    }
    catch(...)
    {
        throw ::IceInternal::LocalExceptionWrapper(::Ice::UnknownException(__FILE__, __LINE__, "unknown c++ exception"), false);
    }
}

void
IceDelegateD::Communication::DataInterface::sendFloatData(const ::Communication::FloatDataPtr& data, const ::Ice::Context* __context)
{
    class _DirectI : public ::IceInternal::Direct
    {
    public:

        _DirectI(const ::Communication::FloatDataPtr& data, const ::Ice::Current& __current) : 
            ::IceInternal::Direct(__current),
            _m_data(data)
        {
        }
        
        virtual ::Ice::DispatchStatus
        run(::Ice::Object* object)
        {
            ::Communication::DataInterface* servant = dynamic_cast< ::Communication::DataInterface*>(object);
            if(!servant)
            {
                throw ::Ice::OperationNotExistException(__FILE__, __LINE__, _current.id, _current.facet, _current.operation);
            }
            servant->sendFloatData(_m_data, _current);
            return ::Ice::DispatchOK;
        }
        
    private:
        
        const ::Communication::FloatDataPtr& _m_data;
    };
    
    ::Ice::Current __current;
    __initCurrent(__current, __Communication__DataInterface__sendFloatData_name, ::Ice::Idempotent, __context);
    try
    {
        _DirectI __direct(data, __current);
        try
        {
            __direct.servant()->__collocDispatch(__direct);
        }
        catch(...)
        {
            __direct.destroy();
            throw;
        }
        __direct.destroy();
    }
    catch(const ::Ice::SystemException&)
    {
        throw;
    }
    catch(const ::IceInternal::LocalExceptionWrapper&)
    {
        throw;
    }
    catch(const ::std::exception& __ex)
    {
        ::IceInternal::LocalExceptionWrapper::throwWrapper(__ex);
    }
    catch(...)
    {
        throw ::IceInternal::LocalExceptionWrapper(::Ice::UnknownException(__FILE__, __LINE__, "unknown c++ exception"), false);
    }
}

Communication::ByteData::ByteData(::Communication::DataTypeIce __ice_type, ::Ice::Long __ice_timeStamp, const ::Communication::byteSequence& __ice_byteArrayData) :
    type(__ice_type),
    timeStamp(__ice_timeStamp),
    byteArrayData(__ice_byteArrayData)
{
}

::Ice::ObjectPtr
Communication::ByteData::ice_clone() const
{
    ::Communication::ByteDataPtr __p = new ::Communication::ByteData(*this);
    return __p;
}

static const ::std::string __Communication__ByteData_ids[2] =
{
    "::Communication::ByteData",
    "::Ice::Object"
};

bool
Communication::ByteData::ice_isA(const ::std::string& _s, const ::Ice::Current&) const
{
    return ::std::binary_search(__Communication__ByteData_ids, __Communication__ByteData_ids + 2, _s);
}

::std::vector< ::std::string>
Communication::ByteData::ice_ids(const ::Ice::Current&) const
{
    return ::std::vector< ::std::string>(&__Communication__ByteData_ids[0], &__Communication__ByteData_ids[2]);
}

const ::std::string&
Communication::ByteData::ice_id(const ::Ice::Current&) const
{
    return __Communication__ByteData_ids[0];
}

const ::std::string&
Communication::ByteData::ice_staticId()
{
    return __Communication__ByteData_ids[0];
}

void
Communication::ByteData::__write(::IceInternal::BasicStream* __os) const
{
    __os->writeTypeId(ice_staticId());
    __os->startWriteSlice();
    ::Communication::__write(__os, type);
    __os->write(timeStamp);
    if(byteArrayData.size() == 0)
    {
        __os->writeSize(0);
    }
    else
    {
        __os->write(&byteArrayData[0], &byteArrayData[0] + byteArrayData.size());
    }
    __os->endWriteSlice();
#if defined(_MSC_VER) && (_MSC_VER < 1300) // VC++ 6 compiler bug
    Object::__write(__os);
#else
    ::Ice::Object::__write(__os);
#endif
}

void
Communication::ByteData::__read(::IceInternal::BasicStream* __is, bool __rid)
{
    if(__rid)
    {
        ::std::string myId;
        __is->readTypeId(myId);
    }
    __is->startReadSlice();
    ::Communication::__read(__is, type);
    __is->read(timeStamp);
    ::std::pair<const ::Ice::Byte*, const ::Ice::Byte*> ___byteArrayData;
    __is->read(___byteArrayData);
    ::std::vector< ::Ice::Byte>(___byteArrayData.first, ___byteArrayData.second).swap(byteArrayData);
    __is->endReadSlice();
#if defined(_MSC_VER) && (_MSC_VER < 1300) // VC++ 6 compiler bug
    Object::__read(__is, true);
#else
    ::Ice::Object::__read(__is, true);
#endif
}

// COMPILERFIX: Stream API is not supported with VC++ 6
#if !defined(_MSC_VER) || (_MSC_VER >= 1300)
void
Communication::ByteData::__write(const ::Ice::OutputStreamPtr&) const
{
    Ice::MarshalException ex(__FILE__, __LINE__);
    ex.reason = "type Communication::ByteData was not generated with stream support";
    throw ex;
}

void
Communication::ByteData::__read(const ::Ice::InputStreamPtr&, bool)
{
    Ice::MarshalException ex(__FILE__, __LINE__);
    ex.reason = "type Communication::ByteData was not generated with stream support";
    throw ex;
}
#endif

class __F__Communication__ByteData : public ::Ice::ObjectFactory
{
public:

    virtual ::Ice::ObjectPtr
    create(const ::std::string& type)
    {
        assert(type == ::Communication::ByteData::ice_staticId());
        return new ::Communication::ByteData;
    }

    virtual void
    destroy()
    {
    }
};

static ::Ice::ObjectFactoryPtr __F__Communication__ByteData_Ptr = new __F__Communication__ByteData;

const ::Ice::ObjectFactoryPtr&
Communication::ByteData::ice_factory()
{
    return __F__Communication__ByteData_Ptr;
}

class __F__Communication__ByteData__Init
{
public:

    __F__Communication__ByteData__Init()
    {
        ::IceInternal::factoryTable->addObjectFactory(::Communication::ByteData::ice_staticId(), ::Communication::ByteData::ice_factory());
    }

    ~__F__Communication__ByteData__Init()
    {
        ::IceInternal::factoryTable->removeObjectFactory(::Communication::ByteData::ice_staticId());
    }
};

static __F__Communication__ByteData__Init __F__Communication__ByteData__i;

#ifdef __APPLE__
extern "C" { void __F__Communication__ByteData__initializer() {} }
#endif

void 
Communication::__patch__ByteDataPtr(void* __addr, ::Ice::ObjectPtr& v)
{
    ::Communication::ByteDataPtr* p = static_cast< ::Communication::ByteDataPtr*>(__addr);
    assert(p);
    *p = ::Communication::ByteDataPtr::dynamicCast(v);
    if(v && !*p)
    {
        IceInternal::Ex::throwUOE(::Communication::ByteData::ice_staticId(), v->ice_id());
    }
}

Communication::FloatData::FloatData(::Communication::DataTypeIce __ice_type, ::Ice::Long __ice_timeStamp, const ::Communication::floatSequence& __ice_floatArrayData) :
    type(__ice_type),
    timeStamp(__ice_timeStamp),
    floatArrayData(__ice_floatArrayData)
{
}

::Ice::ObjectPtr
Communication::FloatData::ice_clone() const
{
    ::Communication::FloatDataPtr __p = new ::Communication::FloatData(*this);
    return __p;
}

static const ::std::string __Communication__FloatData_ids[2] =
{
    "::Communication::FloatData",
    "::Ice::Object"
};

bool
Communication::FloatData::ice_isA(const ::std::string& _s, const ::Ice::Current&) const
{
    return ::std::binary_search(__Communication__FloatData_ids, __Communication__FloatData_ids + 2, _s);
}

::std::vector< ::std::string>
Communication::FloatData::ice_ids(const ::Ice::Current&) const
{
    return ::std::vector< ::std::string>(&__Communication__FloatData_ids[0], &__Communication__FloatData_ids[2]);
}

const ::std::string&
Communication::FloatData::ice_id(const ::Ice::Current&) const
{
    return __Communication__FloatData_ids[0];
}

const ::std::string&
Communication::FloatData::ice_staticId()
{
    return __Communication__FloatData_ids[0];
}

void
Communication::FloatData::__write(::IceInternal::BasicStream* __os) const
{
    __os->writeTypeId(ice_staticId());
    __os->startWriteSlice();
    ::Communication::__write(__os, type);
    __os->write(timeStamp);
    if(floatArrayData.size() == 0)
    {
        __os->writeSize(0);
    }
    else
    {
        __os->write(&floatArrayData[0], &floatArrayData[0] + floatArrayData.size());
    }
    __os->endWriteSlice();
#if defined(_MSC_VER) && (_MSC_VER < 1300) // VC++ 6 compiler bug
    Object::__write(__os);
#else
    ::Ice::Object::__write(__os);
#endif
}

void
Communication::FloatData::__read(::IceInternal::BasicStream* __is, bool __rid)
{
    if(__rid)
    {
        ::std::string myId;
        __is->readTypeId(myId);
    }
    __is->startReadSlice();
    ::Communication::__read(__is, type);
    __is->read(timeStamp);
    __is->read(floatArrayData);
    __is->endReadSlice();
#if defined(_MSC_VER) && (_MSC_VER < 1300) // VC++ 6 compiler bug
    Object::__read(__is, true);
#else
    ::Ice::Object::__read(__is, true);
#endif
}

// COMPILERFIX: Stream API is not supported with VC++ 6
#if !defined(_MSC_VER) || (_MSC_VER >= 1300)
void
Communication::FloatData::__write(const ::Ice::OutputStreamPtr&) const
{
    Ice::MarshalException ex(__FILE__, __LINE__);
    ex.reason = "type Communication::FloatData was not generated with stream support";
    throw ex;
}

void
Communication::FloatData::__read(const ::Ice::InputStreamPtr&, bool)
{
    Ice::MarshalException ex(__FILE__, __LINE__);
    ex.reason = "type Communication::FloatData was not generated with stream support";
    throw ex;
}
#endif

class __F__Communication__FloatData : public ::Ice::ObjectFactory
{
public:

    virtual ::Ice::ObjectPtr
    create(const ::std::string& type)
    {
        assert(type == ::Communication::FloatData::ice_staticId());
        return new ::Communication::FloatData;
    }

    virtual void
    destroy()
    {
    }
};

static ::Ice::ObjectFactoryPtr __F__Communication__FloatData_Ptr = new __F__Communication__FloatData;

const ::Ice::ObjectFactoryPtr&
Communication::FloatData::ice_factory()
{
    return __F__Communication__FloatData_Ptr;
}

class __F__Communication__FloatData__Init
{
public:

    __F__Communication__FloatData__Init()
    {
        ::IceInternal::factoryTable->addObjectFactory(::Communication::FloatData::ice_staticId(), ::Communication::FloatData::ice_factory());
    }

    ~__F__Communication__FloatData__Init()
    {
        ::IceInternal::factoryTable->removeObjectFactory(::Communication::FloatData::ice_staticId());
    }
};

static __F__Communication__FloatData__Init __F__Communication__FloatData__i;

#ifdef __APPLE__
extern "C" { void __F__Communication__FloatData__initializer() {} }
#endif

void 
Communication::__patch__FloatDataPtr(void* __addr, ::Ice::ObjectPtr& v)
{
    ::Communication::FloatDataPtr* p = static_cast< ::Communication::FloatDataPtr*>(__addr);
    assert(p);
    *p = ::Communication::FloatDataPtr::dynamicCast(v);
    if(v && !*p)
    {
        IceInternal::Ex::throwUOE(::Communication::FloatData::ice_staticId(), v->ice_id());
    }
}

::Ice::ObjectPtr
Communication::DataInterface::ice_clone() const
{
    throw ::Ice::CloneNotImplementedException(__FILE__, __LINE__);
    return 0; // to avoid a warning with some compilers
}

static const ::std::string __Communication__DataInterface_ids[2] =
{
    "::Communication::DataInterface",
    "::Ice::Object"
};

bool
Communication::DataInterface::ice_isA(const ::std::string& _s, const ::Ice::Current&) const
{
    return ::std::binary_search(__Communication__DataInterface_ids, __Communication__DataInterface_ids + 2, _s);
}

::std::vector< ::std::string>
Communication::DataInterface::ice_ids(const ::Ice::Current&) const
{
    return ::std::vector< ::std::string>(&__Communication__DataInterface_ids[0], &__Communication__DataInterface_ids[2]);
}

const ::std::string&
Communication::DataInterface::ice_id(const ::Ice::Current&) const
{
    return __Communication__DataInterface_ids[0];
}

const ::std::string&
Communication::DataInterface::ice_staticId()
{
    return __Communication__DataInterface_ids[0];
}

::Ice::DispatchStatus
Communication::DataInterface::___sendByteData(::IceInternal::Incoming& __inS, const ::Ice::Current& __current)
{
    __checkMode(::Ice::Idempotent, __current.mode);
    ::IceInternal::BasicStream* __is = __inS.is();
    __is->startReadEncaps();
    ::Communication::ByteDataPtr data;
    __is->read(::Communication::__patch__ByteDataPtr, &data);
    __is->readPendingObjects();
    __is->endReadEncaps();
    sendByteData(data, __current);
    return ::Ice::DispatchOK;
}

::Ice::DispatchStatus
Communication::DataInterface::___sendFloatData(::IceInternal::Incoming& __inS, const ::Ice::Current& __current)
{
    __checkMode(::Ice::Idempotent, __current.mode);
    ::IceInternal::BasicStream* __is = __inS.is();
    __is->startReadEncaps();
    ::Communication::FloatDataPtr data;
    __is->read(::Communication::__patch__FloatDataPtr, &data);
    __is->readPendingObjects();
    __is->endReadEncaps();
    sendFloatData(data, __current);
    return ::Ice::DispatchOK;
}

static ::std::string __Communication__DataInterface_all[] =
{
    "ice_id",
    "ice_ids",
    "ice_isA",
    "ice_ping",
    "sendByteData",
    "sendFloatData"
};

::Ice::DispatchStatus
Communication::DataInterface::__dispatch(::IceInternal::Incoming& in, const ::Ice::Current& current)
{
    ::std::pair< ::std::string*, ::std::string*> r = ::std::equal_range(__Communication__DataInterface_all, __Communication__DataInterface_all + 6, current.operation);
    if(r.first == r.second)
    {
        throw ::Ice::OperationNotExistException(__FILE__, __LINE__, current.id, current.facet, current.operation);
    }

    switch(r.first - __Communication__DataInterface_all)
    {
        case 0:
        {
            return ___ice_id(in, current);
        }
        case 1:
        {
            return ___ice_ids(in, current);
        }
        case 2:
        {
            return ___ice_isA(in, current);
        }
        case 3:
        {
            return ___ice_ping(in, current);
        }
        case 4:
        {
            return ___sendByteData(in, current);
        }
        case 5:
        {
            return ___sendFloatData(in, current);
        }
    }

    assert(false);
    throw ::Ice::OperationNotExistException(__FILE__, __LINE__, current.id, current.facet, current.operation);
}

void
Communication::DataInterface::__write(::IceInternal::BasicStream* __os) const
{
    __os->writeTypeId(ice_staticId());
    __os->startWriteSlice();
    __os->endWriteSlice();
#if defined(_MSC_VER) && (_MSC_VER < 1300) // VC++ 6 compiler bug
    Object::__write(__os);
#else
    ::Ice::Object::__write(__os);
#endif
}

void
Communication::DataInterface::__read(::IceInternal::BasicStream* __is, bool __rid)
{
    if(__rid)
    {
        ::std::string myId;
        __is->readTypeId(myId);
    }
    __is->startReadSlice();
    __is->endReadSlice();
#if defined(_MSC_VER) && (_MSC_VER < 1300) // VC++ 6 compiler bug
    Object::__read(__is, true);
#else
    ::Ice::Object::__read(__is, true);
#endif
}

// COMPILERFIX: Stream API is not supported with VC++ 6
#if !defined(_MSC_VER) || (_MSC_VER >= 1300)
void
Communication::DataInterface::__write(const ::Ice::OutputStreamPtr&) const
{
    Ice::MarshalException ex(__FILE__, __LINE__);
    ex.reason = "type Communication::DataInterface was not generated with stream support";
    throw ex;
}

void
Communication::DataInterface::__read(const ::Ice::InputStreamPtr&, bool)
{
    Ice::MarshalException ex(__FILE__, __LINE__);
    ex.reason = "type Communication::DataInterface was not generated with stream support";
    throw ex;
}
#endif

void 
Communication::__patch__DataInterfacePtr(void* __addr, ::Ice::ObjectPtr& v)
{
    ::Communication::DataInterfacePtr* p = static_cast< ::Communication::DataInterfacePtr*>(__addr);
    assert(p);
    *p = ::Communication::DataInterfacePtr::dynamicCast(v);
    if(v && !*p)
    {
        IceInternal::Ex::throwUOE(::Communication::DataInterface::ice_staticId(), v->ice_id());
    }
}
