IKVM


https://www.ikvm.net/index.html

Introduction
IKVM.NET is an implementation of Java for Mono and the Microsoft .NET Framework. It includes the following components:
	•	A Java Virtual Machine implemented in .NET
	•	A .NET implementation of the Java class libraries
	•	Tools that enable Java and .NET interoperability


User's_Guide 
IKVM.NET is a Java Virtual Machine (JVM) for the .NET and Mono runtimes. At a time when most people in the computer industry consider Java and .NET as mutually exclusive technologies, IKVM.NET stands in the unique position of bringing them together. Initially born out of frustration with the limitations of tools like JUMP and J#, IKVM.NET was created when Jeroen Frijters set out to create a way to migrate an existing Java database application to .NET. 
IKVM.NET has gone through a variety of designs and name changes to emerge as a sophisticated collection of tools offering a variety of integration patterns between the Java and .NET languages and platforms. It is still under development but people have reported success in running sophisticated applications and tools including Eclipse, JmDNS, JGroups, Jetty (with a few changes), etc. See our [List_of_compatible_libraries]. 
Overview
There are two main ways of using IKVM.NET: 
	•	Dynamically: In this mode, Java classes and jars are used directly to execute Java applications on the .NET runtime. Java bytecode is translated on the fly into CIL and no further steps are necessary. The full Java class loader model is supported in this mode. 
	•	Statically: In order to allow Java code to be used by .NET applications, it must be compiled down to a DLL and used directly. The bytecode is translated to CIL and stored in this form. The assemblies can be referenced directly by the .NET applications and the "Java" objects can be used as if they were .NET objects. While the static mode does not support the full Java class loader mechanism, it is possible for statically-compiled code to create a class loader and load classes dynamically. 
IKVM.NET provides the VM-related technologies for byte-code translation and verification, classloading, etc. It is dependent upon the OpenJDK project for implementations of the JDK libraries. 
IKVM.NET is comprised by the different [Components]. 
System Requirements
You must have one of the following .NET frameworks installed: 
	•	Microsoft .NET Framework 2.0 SP1 (or later) Framework (Windows platform) 
	•	Mono 2.0 (or later) (Windows or Linux) 

